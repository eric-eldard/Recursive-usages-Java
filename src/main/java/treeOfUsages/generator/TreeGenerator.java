package treeOfUsages.generator;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testIntegration.TestFinderHelper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;
import treeOfUsages.TreeRenderer;
import treeOfUsages.node.HiddenNode;
import treeOfUsages.node.MethodNode;
import treeOfUsages.node.UsageNode;
import treeOfUsages.node.UsageNodeFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeGenerator extends Task.Backgroundable
{
    private final TreeRenderer renderer = new TreeRenderer();

    private final Map<PsiMethod, MethodNode> uniqueNodes = new HashMap<>();

    private final PsiMethodImpl method;

    private final Plugin plugin;

    private final boolean includeSupers;

    private final boolean includeOverrides;

    private ProgressIndicator indicator;

    public TreeGenerator(Plugin plugin, Project project, PsiMethodImpl method, boolean includeSupers, 
        boolean includeOverrides)
    {
        super(project, "Generating tree of usages", false);
        this.plugin = plugin;
        this.includeSupers = includeSupers;
        this.includeOverrides = includeOverrides;
        this.method = method;
    }

    public void run(@NotNull ProgressIndicator progressIndicator)
    {
        indicator = progressIndicator;
        indicator.setFraction(0.0);

        while (!ProgressIndicatorUtils.runInReadActionWithWriteActionPriority(this::process))
        {
            ProgressIndicatorUtils.yieldToPendingWriteActions();
        }

        indicator.setFraction(1.0);
    }

    private void process()
    {
        try
        {
            Tree tree = generateUsageTree(method);

            plugin.finishCreatingTree(tree);
        }
        catch (ProcessCanceledException e)
        {
            if (!plugin.userCanceled())
            {
                throw e;
            }
        }
    }

    private Tree generateUsageTree(PsiMethodImpl element) throws ProcessCanceledException
    {
        MethodNode classNode = (MethodNode) UsageNodeFactory.createMethodNode(element, 0, 1);
        DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(classNode);
        recursiveGenerator(methodNode);

        List<PsiMethod> parentAndChildMethods = new ArrayList<>();
        
        if (includeSupers)
        {
            PsiMethod[] superMethods = element.findSuperMethods();
            parentAndChildMethods.addAll(Arrays.asList(superMethods));
        }
        
        if (includeOverrides)
        {
            Query<PsiMethod> shallowOverrides = OverridingMethodsSearch.search(element, false);
            parentAndChildMethods.addAll(shallowOverrides.findAll());
        }

        // include immediate parent/interface and child usages
        List<DefaultMutableTreeNode> parentAndChildNodes = parentAndChildMethods.stream()
            .map(psiMethod -> UsageNodeFactory.createMethodNode(psiMethod, 0, 1))
            .map(usageNode -> recursiveGenerator(new DefaultMutableTreeNode(usageNode)))
            .toList();
        
        DefaultMutableTreeNode rootNode;
        if (parentAndChildNodes.isEmpty())
        {
            rootNode = methodNode; // no siblings, so no need for an invisible root node
        }
        else
        {
            rootNode = new DefaultMutableTreeNode(new HiddenNode());
            rootNode.add(methodNode);
            parentAndChildNodes.forEach(rootNode::add);
        }

        return configureTree(rootNode);
    }

    private DefaultMutableTreeNode recursiveGenerator(DefaultMutableTreeNode parentNode)
        throws ProcessCanceledException
    {
        PsiElement currentElement = ((UsageNode) parentNode.getUserObject()).getElement();
        Query<PsiReference> query = ReferencesSearch.search(currentElement);

        SortedMultiset<NavigatablePsiElement> psiElements = TreeMultiset.create(Comparator
            .<NavigatablePsiElement, Boolean>comparing(TestFinderHelper::isTest) // sort tests to bottom
            .thenComparing(element -> element.getContainingFile().getName())     // then sort by containing file
            .thenComparing(NavigatablePsiElement::getName,                       // then sort by method name
                Comparator.nullsFirst(Comparator.naturalOrder()))                // non-methods usages have null name
            .thenComparingInt(NavigatablePsiElement::hashCode) // distinguish between overloads of same method
        );

        for (PsiReference psiReference : query)
        {
            indicator.checkCanceled();
            PsiElement psiElement = psiReference.getElement();
            addElementIfSupported(psiElement, psiElements);
        }

        for (NavigatablePsiElement psiElement : psiElements.elementSet())
        {
            indicator.checkCanceled();
            
            if (psiElement instanceof PsiMethodImpl methodImpl)
            {
                int count = psiElements.count(psiElement);
                processMethodUsage(parentNode, methodImpl, count);
            }
            else if (psiElement instanceof PsiMethodReferenceExpressionImpl || // field-level usage
                psiElement instanceof PsiReferenceExpressionImpl)              // static initializer usage
            {
                UsageNode caller = UsageNodeFactory.createFileNode(psiElement);
                DefaultMutableTreeNode callingNode = new DefaultMutableTreeNode(caller);

                parentNode.add(callingNode);
            }
        }

        return parentNode;
    }

    private void processMethodUsage(DefaultMutableTreeNode parentNode, PsiMethodImpl methodImpl, int occurrences)
    {
        MethodNode callingMethod = (MethodNode) UsageNodeFactory.createMethodNode(
            methodImpl,
            ((MethodNode) parentNode.getUserObject()).getDepth() + 1,
            occurrences
        );
        flagDuplicateMethodNodes(callingMethod);

        DefaultMutableTreeNode callingNode = new DefaultMutableTreeNode(callingMethod);
        parentNode.add(callingNode);

        // Detect cyclical ancestors (null parent signals you've reached the root node)
        DefaultMutableTreeNode identicalAncestorNode = parentNode;
        while (identicalAncestorNode != null && !sameTreeNode(callingNode, identicalAncestorNode))
        {
            identicalAncestorNode = (DefaultMutableTreeNode) identicalAncestorNode.getParent();
        }

        if (identicalAncestorNode == null)
        {
            // no cyclical ancestor; continue recursing
            recursiveGenerator(callingNode);
        }
        else
        {
            // stop recursing and mark as cyclic
            callingMethod.markCyclic();
        }
    }

    private boolean sameTreeNode(DefaultMutableTreeNode nodeA, DefaultMutableTreeNode nodeB)
    {
        NavigatablePsiElement elementA = ((UsageNode) nodeA.getUserObject()).getElement();
        NavigatablePsiElement elementB = ((UsageNode) nodeB.getUserObject()).getElement();
        return elementA.equals(elementB);
    }

    private void flagDuplicateMethodNodes(MethodNode node)
    {
        PsiMethod method = ((PsiMethod) node.getElement());
        MethodNode previouslySeenNode = uniqueNodes.get(method);
        
        if (previouslySeenNode != null)
        {
            if (node.getDepth() < previouslySeenNode.getDepth())
            {
                // New node is higher in the hierarchy; set it as the "original" and he previous node as the duplicate
                uniqueNodes.put(method, node);
                previouslySeenNode.markDuplicateNode();
            }
            else
            {
                // New node is lower in the hierarchy; treat it as the duplicate
                node.markDuplicateNode();
            }
        }
        else
        {
            uniqueNodes.put(method, node);
        }
    }

    /**
     * Add the element to the collection if it's of a type we can process and show in the tree
     */
    private void addElementIfSupported(PsiElement element, SortedMultiset<NavigatablePsiElement> psiElements)
    {
        PsiFile file = element.getContainingFile();
        int offset = element.getTextOffset();

        PsiMethodImpl method = PsiTreeUtil.findElementOfClassAtOffset(file, offset, PsiMethodImpl.class, false);
        if (method != null)
        {
            psiElements.add(method);
            return;
        }

        PsiMethodReferenceExpressionImpl methodReference =
            PsiTreeUtil.findElementOfClassAtOffset(file, offset, PsiMethodReferenceExpressionImpl.class, false);
        if (methodReference != null)
        {
            psiElements.add(methodReference);
            return;
        }

        PsiReferenceExpressionImpl refExpression =
            PsiTreeUtil.findElementOfClassAtOffset(file, offset, PsiReferenceExpressionImpl.class, false);
        if (refExpression != null)
        {
            psiElements.add(refExpression);
            return;
        }

        System.out.println("DEBUG: Unhandled element type: " + element);
    }

    private Tree configureTree(DefaultMutableTreeNode top)
    {
        Tree tree = new Tree(top);

        tree.setCellRenderer(renderer);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(treeSelectionEvent ->
        {
            TreePath tp = treeSelectionEvent.getPath();
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tp.getLastPathComponent();

            UsageNode mn = (UsageNode) selected.getUserObject();
            NavigatablePsiElement methodImpl = mn.getElement();

            PsiElement navigationElement = methodImpl.getNavigationElement();
            Navigatable navigatable = (Navigatable) navigationElement;
            navigatable.navigate(false);
        });

        return tree;
    }
}
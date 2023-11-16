package treeOfUsages.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

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
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testIntegration.TestFinderHelper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import treeOfUsages.Plugin;
import treeOfUsages.TreeRenderer;
import treeOfUsages.node.MethodNode;
import treeOfUsages.node.HiddenNode;
import treeOfUsages.node.UsageNode;
import treeOfUsages.node.UsageNodeFactory;

public class TreeGenerator extends Task.Backgroundable
{
    private final TreeRenderer renderer;

    private final PsiMethodImpl element;

    private final Plugin plugin;

    private final boolean includeSupers;

    private ProgressIndicator indicator;

    public TreeGenerator(Plugin plugin, @Nullable Project project, PsiMethodImpl e, boolean includeSupers)
    {
        super(project, "Generating Tree of Usages", false);
        this.plugin = plugin;
        this.includeSupers = includeSupers;
        renderer = new TreeRenderer();
        element = e;
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
            Tree tree = generateUsageTree(element);

            plugin.finishCreatingTree(tree);
        }
        catch (ProcessCanceledException e)
        {
            if (plugin.forcedCancel)
            {
                plugin.forcedCancel = false;
            }
            else
            {
                throw e;
            }
        }
    }

    private Tree generateUsageTree(PsiMethodImpl element) throws ProcessCanceledException
    {
        MethodNode classNode = (MethodNode) UsageNodeFactory.createMethodNode(element, 1);
        DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(classNode);
        recursiveGenerator(methodNode);

        PsiMethod[] superMethods = element.findSuperMethods();
        List<DefaultMutableTreeNode> parents = new ArrayList<>();
        if (includeSupers)
        {
            // include immediate parent/interface usages
            Arrays.stream(superMethods)
                .map(psiMethod -> UsageNodeFactory.createMethodNode(psiMethod, 1))
                .map(usageNode -> recursiveGenerator(new DefaultMutableTreeNode(usageNode)))
                .forEach(parents::add);
        }
        
        DefaultMutableTreeNode rootNode;
        if (parents.isEmpty())
        {
            rootNode = methodNode;
        }
        else
        {
            rootNode = new DefaultMutableTreeNode(new HiddenNode());
            rootNode.add(methodNode);
            parents.forEach(rootNode::add);
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
                Comparator.nullsFirst(Comparator.naturalOrder()))                // uses outside methods have null name
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
        MethodNode callingMethod = (MethodNode) UsageNodeFactory.createMethodNode(methodImpl, occurrences);
        DefaultMutableTreeNode callingNode = new DefaultMutableTreeNode(callingMethod);

        parentNode.add(callingNode);

        // Detect cyclical ancestors (null parent signals you've reached the root node)
        DefaultMutableTreeNode identicalAncestorNode = parentNode;
        while (identicalAncestorNode != null && !sameNode(callingNode, identicalAncestorNode))
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
            callingMethod.setCyclic(true);
        }
    }

    private boolean sameNode(@NotNull DefaultMutableTreeNode nodeA, @NotNull DefaultMutableTreeNode nodeB)
    {
        NavigatablePsiElement elementA = ((UsageNode) nodeA.getUserObject()).getElement();
        NavigatablePsiElement elementB = ((UsageNode) nodeB.getUserObject()).getElement();
        return elementA.equals(elementB);
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
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
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
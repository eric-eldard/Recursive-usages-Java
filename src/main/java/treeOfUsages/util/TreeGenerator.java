package treeOfUsages.util;

import java.util.Comparator;

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
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testIntegration.TestFinderHelper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import treeOfUsages.Plugin;
import treeOfUsages.TreeRenderer;
import treeOfUsages.node.ClassNode;
import treeOfUsages.node.UsageNode;
import treeOfUsages.node.UsageNodeFactory;

// TODO Add support for inheritance
public class TreeGenerator extends Task.Backgroundable
{
    private final TreeRenderer renderer;

    private final PsiMethodImpl element;

    private final Plugin mtw;

    private ProgressIndicator indicator;

    public TreeGenerator(Plugin tw, @Nullable Project project, PsiMethodImpl e)
    {
        super(project, "Generating Tree of Usages", false);
        mtw = tw;
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

            mtw.finishCreatingTree(tree);
        }
        catch (ProcessCanceledException e)
        {
            if (mtw.forcedCancel)
            {
                mtw.forcedCancel = false;
            }
            else
            {
                throw e;
            }
        }
    }

    private Tree generateUsageTree(PsiMethodImpl element) throws ProcessCanceledException
    {
        ClassNode classNode = (ClassNode) UsageNodeFactory.createMethodNode(element, 1);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(classNode);
        recursiveGenerator(rootNode);

        return configureTree(rootNode);
    }

    private DefaultMutableTreeNode recursiveGenerator(DefaultMutableTreeNode parentNode)
        throws ProcessCanceledException
    {
        PsiElement currentElement = ((UsageNode) parentNode.getUserObject()).getElement();
        Query<PsiReference> query = ReferencesSearch.search(currentElement);

        SortedMultiset<NavigatablePsiElement> psiElements = TreeMultiset.create(Comparator
            .comparing(TestFinderHelper::isTest)                             // sort tests to bottom
            .thenComparing(elem -> elem.getContainingFile().getName())       // then sort by containing file
            .thenComparing(elem -> ((NavigatablePsiElement) elem).getName()) // then sort by method name
            .thenComparingInt(PsiElement::hashCode) // distinguish between overloads of the same method
        );

        for (PsiReference psiReference : query)
        {
            indicator.checkCanceled();
            PsiElement el = psiReference.getElement();
            PsiFile file = el.getContainingFile();
            final int offset = el.getTextOffset();

            PsiMethodImpl methodImpl = PsiTreeUtil.findElementOfClassAtOffset(file, offset, PsiMethodImpl.class, false);

            if (methodImpl != null)
            {
                psiElements.add(methodImpl);
            }
            else
            {
                PsiMethodReferenceExpressionImpl methodReferenceImpl =
                    PsiTreeUtil.findElementOfClassAtOffset(file, offset, PsiMethodReferenceExpressionImpl.class, false);

                if (methodReferenceImpl != null)
                {
                    psiElements.add(methodReferenceImpl);
                }
                else
                {
                    System.out.println("Unrecognized element");
                }
            }
        }

        for (NavigatablePsiElement psiElement : psiElements.elementSet())
        {
            indicator.checkCanceled();
            if (psiElement instanceof PsiMethodImpl methodImpl)
            {
                int occurrences = psiElements.count(psiElement);
                ClassNode callingMethod = (ClassNode) UsageNodeFactory.createMethodNode(methodImpl, occurrences);
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
                    callingMethod.setIsCyclic();
                }
            }
            else
            {
                PsiMethodReferenceExpressionImpl methodReferenceImpl = (PsiMethodReferenceExpressionImpl) psiElement;

                UsageNode caller = UsageNodeFactory.createFileNode(methodReferenceImpl);
                DefaultMutableTreeNode callingNode = new DefaultMutableTreeNode(caller);

                parentNode.add(callingNode);
            }
        }

        return parentNode;
    }
    
    private boolean sameNode(@NotNull DefaultMutableTreeNode nodeA, @NotNull DefaultMutableTreeNode nodeB)
    {
        NavigatablePsiElement elementA = ((UsageNode) nodeA.getUserObject()).getElement();
        NavigatablePsiElement elementB = ((UsageNode) nodeB.getUserObject()).getElement();
        return elementA.equals(elementB);
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

            final PsiElement navigationElement = methodImpl.getNavigationElement();
            Navigatable navigatable = (Navigatable) navigationElement;
            navigatable.navigate(false);
        });

        return tree;
    }
}
package treeOfUsages.node;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.testIntegration.TestFinderHelper;
import com.intellij.ui.RowIcon;
import com.intellij.util.Query;
import lombok.Getter;
import treeOfUsages.ProjectIcons;

/**
 * An invocation of a method from a class
 */
public class MethodNode implements UsageNode
{
    private final PsiMethod method;

    /**
     * The distance down a given branch this node was found
     */
    @Getter
    private final int depth;

    /**
     * The number of times the parent is invoked directly from this node
     */
    @Getter
    private final int count;

    /**
     * This method will eventually call itself, appearing on the stack twice
     */
    @Getter
    private boolean cyclic;

    /**
     * This node matched one already found in an earlier branch of the tree
     */
    @Getter
    private boolean duplicateNode;

    public MethodNode(PsiMethod method, int depth, int count)
    {
        this.method = method;
        this.depth = depth;
        this.count = count;
    }

    @Override
    public NavigatablePsiElement getElement()
    {
        return this.method;
    }

    public void markCyclic()
    {
        cyclic = true;
    }

    public void markDuplicateNode()
    {
        duplicateNode = true;
    }

    @Override
    public Icon getIcon()
    {
        List<Icon> icons = new ArrayList<>();

        if (isDuplicateNode())
        {
            icons.add(AllIcons.Nodes.MultipleTypeDefinitions);

            if (isCyclic())
            {
                icons.add(AllIcons.Gutter.RecursiveMethod);
            }
        }
        else
        {
            if (isTest())
            {
                icons.add(AllIcons.Actions.StartDebugger);
            }
            else
            {
                icons.add(AllIcons.Nodes.Method);
            }

            if (hasParent() && hasChild())
            {
                icons.add(ProjectIcons.SiblingMethodGutter);
            }
            else if (hasParent())
            {
                icons.add(AllIcons.Gutter.ImplementingMethod);
            }
            else if (hasChild())
            {
                icons.add(AllIcons.Gutter.OverridenMethod);
            }
        }

        return new RowIcon(icons.toArray(new Icon[0]));
    }

    @Override
    public String getMainText()
    {
        return getElement().getName();
    }

    @Override
    public String getAdditionalText()
    {
        return " ← " + getElement().getOriginalElement().getContainingFile().getName() + makeMultiplicityLabel();
    }

    /**
     *
     * @return true if this is a test method
     */
    public boolean isTest()
    {
        return TestFinderHelper.isTest(method);
    }

    /**
     * @return true if this method implements at least one interface or overrides a method from any parent class
     */
    public boolean hasParent()
    {
        return method.findSuperMethods().length > 0;
    }

    /**
     * @return true if this method is overridden in at least one other class
     */
    public boolean hasChild()
    {
        Query<PsiMethod> overridingMethods = OverridingMethodsSearch.search(method, false);
        return overridingMethods.findFirst() != null;
    }

    /**
     * @return a multiplicity label for counts greater than 1: " (x2)"
     */
    private String makeMultiplicityLabel()
    {
        return count > 1 ? String.format(" (x%d)", count) : "";
    }
}
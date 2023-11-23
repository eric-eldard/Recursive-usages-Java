package treeOfUsages.node;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.ui.RowIcon;
import com.intellij.util.Query;

/**
 * An invocation of a method from a class
 */
public class MethodNode implements UsageNode
{
    private final PsiMethod method;

    private final Icon icon;

    /**
     * The distance down a given branch this node was found
     */
    private final int depth;

    /**
     * The number of times the parent is invoked directly from this node
     */
    private final int count;

    /**
     * This method will eventually call itself, appearing on the stack twice
     */
    private boolean cyclic;

    /**
     * This node matched one already found in an earlier branch of the tree
     */
    private boolean duplicateNode;

    public MethodNode(Icon icon, PsiMethod method, int depth, int count)
    {
        this.icon = icon;
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

    public int getDepth()
    {
        return depth;
    }

    @Override
    public Icon getIcon()
    {
        List<Icon> icons = new ArrayList<>();

        if (duplicateNode)
        {
            icons.add(AllIcons.Nodes.MultipleTypeDefinitions);
        }
        else
        {
            icons.add(icon);

            if (hasParent() && hasChild())
            {
                icons.add(AllIcons.Gutter.SiblingInheritedMethod);
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

        if (cyclic)
        {
            icons.add(AllIcons.Gutter.RecursiveMethod);
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
     * @return true if this method implements at least one interface or overrides a method from any parent class
     */
    private boolean hasParent()
    {
        return method.findSuperMethods().length > 0;
    }

    /**
     * @return true if this method is overridden in at least one other class
     */
    private boolean hasChild()
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
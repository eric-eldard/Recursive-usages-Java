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
import treeOfUsages.node.icon.HasIcon;

/**
 * An invocation of a method from a class
 */
public class MethodNode implements UsageNode
{
    private final PsiMethod method;

    private final HasIcon iconContainer;

    /**
     * This method will eventually call itself, appearing on the stack twice
     */
    private boolean cyclic;

    /**
     * The number of times the parent is invoked directly from this node
     */
    private final int count;

    public MethodNode(HasIcon iconContainer, PsiMethod method, int count)
    {
        this.iconContainer = iconContainer;
        this.method = method;
        this.count = count;
    }

    @Override
    public NavigatablePsiElement getElement()
    {
        return this.method;
    }

    public boolean isCyclic()
    {
        return isCyclic();
    }

    public void setCyclic(boolean cyclic)
    {
        this.cyclic = cyclic;
    }

    @Override
    public Icon getIcon()
    {
        List<Icon> icons = new ArrayList<>();

        icons.add(iconContainer.getIcon());

        if (hasParent())
        {
            icons.add(AllIcons.Gutter.ImplementingMethod);
        }

        if (hasChild())
        {
            icons.add(AllIcons.Gutter.OverridenMethod);
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
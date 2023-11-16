package treeOfUsages.node;

import javax.swing.Icon;

import com.intellij.psi.NavigatablePsiElement;

public interface UsageNode
{
    NavigatablePsiElement getElement();

    String getMainText();

    String getAdditionalText();

    Icon getIcon();

    /**
     * Should this node be shown in the output tree
     */
    default boolean isHidden()
    {
        return false;
    }
}
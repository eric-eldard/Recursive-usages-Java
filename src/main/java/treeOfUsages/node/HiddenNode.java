package treeOfUsages.node;

import javax.swing.Icon;

import com.intellij.psi.NavigatablePsiElement;

/**
 * For use as invisible Swing root node when there are multiple top-level elements
 */
public class HiddenNode implements UsageNode
{
    @Override
    public NavigatablePsiElement getElement()
    {
        throw new UnsupportedOperationException("not supported for hidden nodes");
    }

    @Override
    public Icon getIcon()
    {
        throw new UnsupportedOperationException("not supported for hidden nodes");
    }

    @Override
    public String getMainText()
    {
        throw new UnsupportedOperationException("not supported for hidden nodes");
    }

    @Override
    public String getAdditionalText()
    {
        throw new UnsupportedOperationException("not supported for hidden nodes");
    }

    @Override
    public boolean isHidden()
    {
        return true;
    }
}
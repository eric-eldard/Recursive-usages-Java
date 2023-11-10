package myToolWindow.Nodes;

import com.intellij.psi.NavigatablePsiElement;
import myToolWindow.Nodes.Icons.HasIcon;

public interface UsageNode extends HasIcon
{
    NavigatablePsiElement getElement();

    String getMainText();

    String getAdditionalText();
}
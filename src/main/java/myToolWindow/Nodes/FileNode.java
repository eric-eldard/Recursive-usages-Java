package myToolWindow.Nodes;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import com.intellij.ui.RowIcon;
import myToolWindow.Nodes.Icons.HasIcon;

import javax.swing.*;

public class FileNode implements UsageNode {
    private final HasIcon iconContainer;
    private final PsiMethodReferenceExpressionImpl element;
    private boolean isCyclic = false;

    public FileNode(HasIcon iconContainer, PsiMethodReferenceExpressionImpl e) {
        this.iconContainer = iconContainer;
        this.element = e;
    }

    @Override
    public Icon getIcon() {
        if (isCyclic) {
            return new RowIcon(iconContainer.getIcon(), AllIcons.Gutter.RecursiveMethod);
        } else {
            return iconContainer.getIcon();
        }
    }

    public void setIsCyclic() {
        isCyclic = true;
    }

    @Override
    public NavigatablePsiElement getElement() throws NullPointerException {
        return this.element;
    }

    @Override
    public String getMainText() throws NullPointerException {
        return getElement().getContainingFile().getName();
    }

    @Override
    public String getAdditionalText() {
        return "";
    }
}

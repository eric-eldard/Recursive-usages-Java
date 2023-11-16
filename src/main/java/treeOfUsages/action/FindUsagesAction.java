package treeOfUsages.action;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;
import treeOfUsages.node.UsageNode;

public class FindUsagesAction extends EnableableAction
{
    public Plugin plugin;

    private boolean enabled = true;

    private boolean includeParents;

    private boolean includeChildren;

    @SuppressWarnings("unused")
    public FindUsagesAction()
    {
    }

    public FindUsagesAction(Plugin plugin, String text, String description, Icon icon, boolean includeParents,
        boolean includeChildren)
    {
        super(text, description, icon);
        this.plugin = plugin;
        this.includeParents = includeParents;
        this.includeChildren = includeChildren;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void update(@NotNull AnActionEvent e)
    {
        e.getPresentation().setEnabled(enabled);
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        // If the plugin is in focus and has a method selected, we'll follow that method.
        PsiMethodImpl method = getMethodFromTree();
        
        // The plugin wasn't in focus or the focused element wasn't a method; we'll follow the method where we find the
        // cursor in the editor.
        if (method == null)
        {
            method = getMethodFromEditor(e.getData(CommonDataKeys.EDITOR));
        }

        // Render tree for the selected method
        if (method != null)
        {
            plugin.createAndRenderTree(method, includeParents, includeChildren);
        }
    }
    
    private PsiMethodImpl getMethodFromTree()
    {
        PsiMethodImpl method = null;

        if (plugin.getTree() != null &&
            plugin.getTree().hasFocus() &&
            plugin.getTree().getSelectionCount() > 0)
        {
            DefaultMutableTreeNode selectedMethod =
                (DefaultMutableTreeNode) plugin.getTree().getSelectionPath().getLastPathComponent();

            UsageNode mn = (UsageNode) selectedMethod.getUserObject();
            NavigatablePsiElement element = mn.getElement();
            if (element instanceof PsiMethodImpl psiMethod)
            {
                method = psiMethod;
            }
        }

        return method;
    }
    
    private PsiMethodImpl getMethodFromEditor(Editor editor)
    {
        PsiMethodImpl method = null;

        if (editor != null)
        {
            CaretModel caretModel = editor.getCaretModel();

            VisualPosition visualPosition = caretModel.getVisualPosition();
            LogicalPosition position = editor.visualToLogicalPosition(visualPosition);

            Document document = editor.getDocument();
            final int offset = editor.logicalPositionToOffset(position);

            Project project = editor.getProject();
            if (project != null)
            {
                PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (file != null)
                {
                    method = PsiTreeUtil.findElementOfClassAtOffset(file, offset, PsiMethodImpl.class, false);
                }
            }
        }
        
        return method;
    }
}
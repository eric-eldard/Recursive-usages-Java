package myToolWindow;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.SimpleTextAttributes;
import myToolWindow.Nodes.UsageNode;
import org.jetbrains.annotations.NotNull;

public class TreeRenderer extends NodeRenderer
{
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded,
        boolean leaf, int row, boolean hasFocus)
    {
        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        UsageNode node = (UsageNode) treeNode.getUserObject();
        if (node != null)
        {
            setIcon(node.getIcon());

            try
            {
                append(node.getMainText(), SimpleTextAttributes.REGULAR_ATTRIBUTES, true);
                append(node.getAdditionalText(), SimpleTextAttributes.GRAYED_ATTRIBUTES, false);
            }
            catch (Exception e)
            {
                append("Error", SimpleTextAttributes.ERROR_ATTRIBUTES);
            }
        }
    }
}
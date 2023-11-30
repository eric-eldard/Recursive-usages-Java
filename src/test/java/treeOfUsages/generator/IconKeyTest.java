package treeOfUsages.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import javax.swing.tree.DefaultMutableTreeNode;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import com.intellij.ui.RowIcon;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.PlatformIcons;
import lombok.SneakyThrows;

import org.junit.Test;
import treeOfUsages.Plugin;
import treeOfUsages.ProjectIcons;
import treeOfUsages.node.ClassNode;
import treeOfUsages.node.MethodNode;
import treeOfUsages.node.UsageNode;

public class IconKeyTest extends LightPlatformCodeInsightFixture4TestCase
{
    @Test
    public void testKeyExpandsFullyByDefault()
    {
        Tree tree = makeIconKeyTree();   
        
        for (int i = 0; i < tree.getRowCount(); i++)
        {
            tree.setSelectionPath(tree.getPathForRow(i));
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            if (node.getChildCount() > 0)
            {
                assertTrue(makeNodeLabel(i, node) + " is not expanded", tree.isExpanded(i));
                System.out.println(makeNodeLabel(i, node) + " is expanded");
            }
            else
            {
                // You can call JTree.expandRow on a node with no children, but it returns false for JTree.isExpanded
                System.out.println(makeNodeLabel(i, node) + " has no children");
            }
        }
    }

    /**
     * This is an appropriate stand-in for "testNodesHaveCorrectIcons()" because 
     * {@link KeyTreeGenerator.ExampleMethodNode#getIcon()} does not override the rules from
     * {@link MethodNode#getIcon()}
     */
    @Test
    public void testKeyNodesHaveCorrectIcons()
    {
        Tree tree = makeIconKeyTree();

        for (int i = 0; i < tree.getRowCount(); i++)
        {
            tree.setSelectionPath(tree.getPathForRow(i));
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            UsageNode usageNode = (UsageNode) node.getUserObject();
            System.out.println("Verifying icons for node: " + usageNode.getMainText());

            if (usageNode instanceof MethodNode methodNode)
            {
                RowIcon icon = (RowIcon) methodNode.getIcon();

                if (methodNode.isDuplicateNode())
                {
                    // Verify main icon
                    assertThat(icon.getIcon(0), is(AllIcons.Nodes.MultipleTypeDefinitions));

                    // Verify gutter icon
                    if (methodNode.isCyclic())
                    {
                        assertThat(icon.getIcon(1), is(AllIcons.Gutter.RecursiveMethod));
                    }
                }
                else
                {
                    // Verify main icon
                    if (methodNode.isTest())
                    {
                        assertThat(icon.getIcon(0), is(AllIcons.Actions.StartDebugger));
                    }
                    else
                    {
                        assertThat(icon.getIcon(0), is(AllIcons.Nodes.Method));
                    }

                    // Verify gutter icons
                    if (methodNode.hasParent() && methodNode.hasChild())
                    {
                        assertThat(icon.getIcon(1), is(ProjectIcons.SiblingMethodGutter));
                    }
                    else if (methodNode.hasParent())
                    {
                        assertThat(icon.getIcon(1), is(AllIcons.Gutter.ImplementingMethod));
                    }
                    else if (methodNode.hasChild())
                    {
                        assertThat(icon.getIcon(1), is(AllIcons.Gutter.OverridenMethod));
                    }
                }
            }
            else if (usageNode instanceof ClassNode classNode)
            {
                assertThat(classNode.getIcon(), is(PlatformIcons.CLASS_ICON));
            }
            else
            {
                fail("Unrecognized node type: " + usageNode.getClass().getSimpleName());
            }
        }
    }
    
    @SneakyThrows
    private Tree makeIconKeyTree()
    {
        Plugin plugin = new Plugin(mock(Project.class));
        plugin.showKey();

        // Tree is built in async process; wait till complete
        Tree tree = null;
        while (tree == null)
        {
            tree = plugin.getTree();
            Thread.sleep(200);
        }
        return tree;
    }

    private String makeNodeLabel(int index, DefaultMutableTreeNode node)
    {
        String nodeText = ((UsageNode) node.getUserObject()).getMainText();
        return "Node #" + index + " (" + nodeText + ')';
    }
}
package treeOfUsages.generator;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;
import treeOfUsages.TreeRenderer;
import treeOfUsages.node.MethodNode;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class KeyTreeGenerator extends Task.Backgroundable
{
    private static final String SOME_CLASS = "SomeClass";

    private final Plugin plugin;

    public KeyTreeGenerator(Plugin plugin, Project project)
    {
        super(project, "Generating Tree of Usages key", false);
        this.plugin = plugin;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator)
    {
        indicator.setFraction(0.0);
        
        MethodNode rootMethod = new ExampleMethodNode(
            "your method",
            SOME_CLASS);
        MethodNode callingMethod = new ExampleMethodNode(
            "a method that calls your method",
            SOME_CLASS);
        MethodNode multiUsageMethod = new ExampleMethodNode(
            "a method that calls your method twice",
            SOME_CLASS + " (x2)");
        MethodNode duplicateBranchMethod = new ExampleMethodNode(
            "a duplicate method already represented in another branch",
            SOME_CLASS);
        duplicateBranchMethod.markDuplicateNode();
        MethodNode cyclicalBranchMethod = new ExampleMethodNode(
            "a duplicate method already represented in the same branch (a cyclical call chain)",
            SOME_CLASS);
        cyclicalBranchMethod.markDuplicateNode();
        cyclicalBranchMethod.markCyclic();
        ExampleMethodNode implementingMethod = new ExampleMethodNode(
            "this method implements or overrides another; you may want to run a tree on it too, with parents included",
            SOME_CLASS);
        implementingMethod.markHasParent();
        ExampleMethodNode overriddenMethod = new ExampleMethodNode(
            "this method is overridden by another; you may want to run a tree on it too, with children included",
            SOME_CLASS);
        overriddenMethod.markHasChild();
        ExampleMethodNode siblingMethod = new ExampleMethodNode(
            "a method that both implements/overrides and is overridden",
            SOME_CLASS);
        siblingMethod.markHasParent();
        siblingMethod.markHasChild();
        MethodNode testMethod = new ExampleMethodNode(
            "a test method that calls your method",
            "SomeTestClass",
            AllIcons.Actions.StartDebugger);
        MethodNode classMethod = new ExampleMethodNode(
            "a class-level usage of your method to initialize a field or from within a static initializer",
            null,
            PlatformIcons.CLASS_ICON);

        DefaultMutableTreeNode rootNode  = new DefaultMutableTreeNode(rootMethod);
        rootNode.add(new DefaultMutableTreeNode(callingMethod));
        DefaultMutableTreeNode multiUsageNode = new DefaultMutableTreeNode(multiUsageMethod);
        rootNode.add(multiUsageNode);
        DefaultMutableTreeNode duplicateBranchNode = new DefaultMutableTreeNode(duplicateBranchMethod);
        multiUsageNode.add(duplicateBranchNode);
        duplicateBranchNode.add(new DefaultMutableTreeNode(cyclicalBranchMethod));
        rootNode.add(new DefaultMutableTreeNode(implementingMethod));
        rootNode.add(new DefaultMutableTreeNode(overriddenMethod));
        rootNode.add(new DefaultMutableTreeNode(siblingMethod));
        rootNode.add(new DefaultMutableTreeNode(testMethod));
        rootNode.add(new DefaultMutableTreeNode(classMethod));

        Tree tree = new Tree(rootNode);
        tree.setCellRenderer(new TreeRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        plugin.finishCreatingTree(tree);
        
        indicator.setFraction(1.0);
    }

    private static class ExampleMethodNode extends MethodNode
    {
        private final String mainText;
        private final String additionalText;
        private boolean hasParent;
        private boolean hasChild;

        public ExampleMethodNode(String mainText, String additionalText)
        {
            super(AllIcons.Nodes.Method, null, 0, 1);
            this.mainText = mainText;
            this.additionalText = additionalText;
        }

        public ExampleMethodNode(String mainText, String additionalText, Icon icon)
        {
            super(icon, null, 0, 1);
            this.mainText = mainText;
            this.additionalText = additionalText;
        }

        @Override
        public String getMainText()
        {
            return mainText;
        }

        @Override
        public String getAdditionalText()
        {
            return additionalText == null ? "" : " ← " + additionalText;
        }

        @Override
        protected boolean hasParent()
        {
            return hasParent;
        }

        @Override
        protected boolean hasChild()
        {
            return hasChild;
        }

        private void markHasParent()
        {
            this.hasParent = true;
        }

        private void markHasChild()
        {
            this.hasChild = true;
        }
    }
}
package treeOfUsages.util;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.testIntegration.TestFinderHelper;
import com.intellij.util.PlatformIcons;
import treeOfUsages.node.ClassNode;
import treeOfUsages.node.MethodNode;
import treeOfUsages.node.UsageNode;

public class UsageNodeFactory
{
    public static UsageNode createFileNode(NavigatablePsiElement ref)
    {
        return new ClassNode(PlatformIcons.CLASS_ICON, ref);
    }

    public static UsageNode createMethodNode(PsiMethod method, int depth, int count)
    {
        Icon icon = TestFinderHelper.isTest(method) ?
            AllIcons.Actions.StartDebugger : 
            AllIcons.Nodes.Method;
        return new MethodNode(icon, method, depth, count);
    }
}
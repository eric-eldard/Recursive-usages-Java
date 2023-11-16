package treeOfUsages.node;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.testIntegration.TestFinderHelper;
import treeOfUsages.node.icon.HasIcon;
import treeOfUsages.node.icon.file.JavaFileIcon;
import treeOfUsages.node.icon.method.MethodIcon;
import treeOfUsages.node.icon.method.TestIcon;

public class UsageNodeFactory
{
    public static UsageNode createFileNode(NavigatablePsiElement ref)
    {
        return new ClassNode(new JavaFileIcon(), ref);
    }

    public static UsageNode createMethodNode(PsiMethod method, int count)
    {
        HasIcon icon = TestFinderHelper.isTest(method) ? new TestIcon() : new MethodIcon();
        return new MethodNode(icon, method, count);
    }
}
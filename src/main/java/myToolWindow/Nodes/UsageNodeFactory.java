package myToolWindow.Nodes;

import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import com.intellij.testIntegration.TestFinderHelper;
import myToolWindow.Nodes.Icons.ClassNodes.MethodNode;
import myToolWindow.Nodes.Icons.ClassNodes.TestNode;
import myToolWindow.Nodes.Icons.FileNodes.JavaFileNode;

public class UsageNodeFactory
{
    public static UsageNode createFileNode(PsiMethodReferenceExpressionImpl ref)
    {
        return new FileNode(new JavaFileNode(), ref);
    }

    public static UsageNode createMethodNode(PsiMethodImpl method, int count)
    {
        if (TestFinderHelper.isTest(method))
        {
            return new ClassNode(new TestNode(), method, count);
        }
        return new ClassNode(new MethodNode(), method, count);
    }
}
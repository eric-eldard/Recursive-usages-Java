package myToolWindow.Nodes;

import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import myToolWindow.Nodes.Icons.ClassNodes.MethodNode;
import myToolWindow.Nodes.Icons.ClassNodes.TestNode;
import myToolWindow.Nodes.Icons.FileNodes.JavaFileNode;

import com.intellij.testIntegration.TestFinderHelper;

public class UsageNodeFactory
{
    public static UsageNode createFileNode(PsiMethodReferenceExpressionImpl ref)
    {
        return new FileNode(new JavaFileNode(), ref);
    }

    public static UsageNode createMethodNode(PsiMethodImpl mel)
    {
        if (TestFinderHelper.isTest(mel))
        {
            return new ClassNode(new TestNode(), mel);
        }
        return new ClassNode(new MethodNode(), mel);
    }
}
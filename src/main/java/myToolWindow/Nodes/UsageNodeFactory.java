package myToolWindow.Nodes;

import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import myToolWindow.Nodes.Icons.ClassNodes.MethodNode;
import myToolWindow.Nodes.Icons.FileNodes.JavaFileNode;
import org.jetbrains.annotations.Contract;

public class UsageNodeFactory
{
    @Contract("_ -> new")
    public static UsageNode createFileNode(PsiMethodReferenceExpressionImpl ref)
    {
        return new FileNode(new JavaFileNode(), ref);
    }

    @Contract("_ -> new")
    public static UsageNode createMethodNode(PsiMethodImpl mel)
    {
        return new ClassNode(new MethodNode(), mel);
    }
}
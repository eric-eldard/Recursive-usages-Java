package toolOfUsages.node;

import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodReferenceExpressionImpl;
import com.intellij.testIntegration.TestFinderHelper;
import toolOfUsages.node.icon.method.MethodIcon;
import toolOfUsages.node.icon.method.TestIcon;
import toolOfUsages.node.icon.file.JavaFileIcon;
import toolOfUsages.node.icon.HasIcon;

public class UsageNodeFactory
{
    public static UsageNode createFileNode(PsiMethodReferenceExpressionImpl ref)
    {
        return new FileNode(new JavaFileIcon(), ref);
    }

    public static UsageNode createMethodNode(PsiMethodImpl method, int count)
    {
        HasIcon icon = TestFinderHelper.isTest(method) ? new TestIcon() : new MethodIcon();
        return new ClassNode(icon, method, count);
    }
}
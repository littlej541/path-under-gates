package cyberslas.pathundergates.asm;

import cyberslas.pathundergates.PathUnderGates;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("@mcversion@")
@IFMLLoadingPlugin.TransformerExclusions({"cyberslas.pathundergates.asm"})
public class PUGFMLLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{GrassPathTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return PathUnderGates.class.getName();
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

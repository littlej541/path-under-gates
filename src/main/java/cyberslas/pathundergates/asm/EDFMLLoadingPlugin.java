package cyberslas.pathundergates.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"cyberslas.pathundergates.asm"})
public class EDFMLLoadingPlugin implements IFMLLoadingPlugin {
    public static final String MODID = "@modid@-core";
    public static final String VERSION = "@version@";

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{GrassPathTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
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

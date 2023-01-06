package cyberslas.pathundergates.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class DomainNamePair extends Pair<String, String> {
    public DomainNamePair(String domain, String name) {
        super(domain, name);
    }

    public DomainNamePair(ResourceLocation resource) {
        super(resource.getNamespace(), resource.getPath());
    }

    public String getDomain() {
        return this.getFirst();
    }

    public String getName() {
        return this.getSecond();
    }
}
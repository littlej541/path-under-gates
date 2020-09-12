package cyberslas.pathundergates.util;

import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class DomainNamePair {
    public String domain;
    public String name;

    public DomainNamePair(String domain, String name) {
        this.domain = domain;
        this.name = name;
    }

    public DomainNamePair(ResourceLocation resource) {
        this.domain = resource.getNamespace();
        this.name = resource.getPath();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof DomainNamePair && ((DomainNamePair) other).domain.equals(this.domain) && ((DomainNamePair) other).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.domain, this.name);
    }
}
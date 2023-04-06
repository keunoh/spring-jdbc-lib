package org.springframework.jdbc.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortedResourcesFactoryBean extends AbstractFactoryBean<Resource[]> implements ResourceLoaderAware {

    private final List<String> locations;

    private ResourcePatternResolver resourcePatternResolver;

    public SortedResourcesFactoryBean(List<String> locations) {
        this.locations = locations;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    public SortedResourcesFactoryBean(ResourceLoader resourceLoader, List<String> locations) {
        this.locations = locations;
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    }

    @Override
    public Class<? extends Resource[]> getObjectType() { return Resource[].class; }

    @Override
    protected Resource[] createInstance() throws Exception {
        List<Resource> scripts = new ArrayList<>();
        for (String location : this.locations) {
            List<Resource> resources = new ArrayList<>(
                    Arrays.asList(this.resourcePatternResolver.getResources(location)));
            resources.sort((r1, r2) -> {
                try {
                    return r1.getURL().toString().compareTo(r2.getURL().toString());
                }
                catch (IOException ex) {
                    return 0;
                }
            });
            scripts.addAll(resources);
        }
        return scripts.toArray(new Resource[0]);
    }
}

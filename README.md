# FastStats - FabricMC

A client implementation of [FastStats](https://faststats.dev/) for FabricMC

## Usage

```groovy
dependencies {
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "FABRIC_API_VERSION")) // Include this to access to the required Minecraft Server
    
    implementation("io.github.projectunifed:faststats-fabric:<VERSION>+<MC_VERSION>") // 0.1.0+1.21.11, 0.1.0+26.1.2
}
```

```java
public class ExampleMod implements ModInitializer {
    private Metrics metrics;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            metrics = Metrics.builder()
                    .platform(new FabricPlatform(server, "mod-id"))
                    .serializer(new GsonSerializer())
                    .submitter(new NetSubmitter("YOUR_TOKEN"))
                    .addMetric(Metric.string("my_feature", () -> "enabled"))
                    .build();
            metrics.start();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (metrics != null) {
                metrics.shutdown();
            }
        });
    }
}
```

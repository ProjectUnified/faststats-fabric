# FastStats - FabricMC

A client implementation of [FastStats](https://faststats.dev/) for FabricMC

## Usage

```groovy
dependencies {
  implementation("io.github.projectunifed:faststats-fabric:<VERSION>+<MC_VERSION>")
}
```

```java
public class ExampleMod implements ModInitializer {
    // context-aware error tracker, automatically tracks errors in the same class loader
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();

    // context-unaware error tracker, does not automatically track errors
    public static final ErrorTracker CONTEXT_UNAWARE_ERROR_TRACKER = ErrorTracker.contextUnaware();

    private final Metrics metrics = FabricMetrics.factory()
            .url(URI.create("https://metrics.example.com/v1/collect")) // For self-hosted metrics servers only

            // Custom example metrics
            // For this to work you have to create a corresponding data source in your project settings first
            .addMetric(Metric.number("example_metric", () -> 42))
            .addMetric(Metric.string("example_string", () -> "Hello, World!"))
            .addMetric(Metric.bool("example_boolean", () -> true))
            .addMetric(Metric.stringArray("example_string_array", () -> new String[]{"Option 1", "Option 2"}))
            .addMetric(Metric.numberArray("example_number_array", () -> new Number[]{1, 2, 3}))
            .addMetric(Metric.booleanArray("example_boolean_array", () -> new Boolean[]{true, false}))

            // Attach an error tracker
            // This must be enabled in the project settings
            .errorTracker(ERROR_TRACKER)

            .debug(true) // Enable debug mode for development and testing

            .token("YOUR_TOKEN_HERE") // required -> token can be found in the settings of your project
            .create("example-mod"); // your mod id as defined in fabric.mod.json
}
```
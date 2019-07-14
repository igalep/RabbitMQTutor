package stepdefinition;

public class Crossplatform {
    private static GlobalStepDefinition globalStepDefinition;

    public static void main(String[] argv) throws Exception {
        globalStepDefinition = new GlobalStepDefinition();

        Hook hook = new Hook(globalStepDefinition);
        hook.LocalsetUp();
    }
}

package net.corda.tools.shell;

// A simple forwarder to the "flow start" command, for easier typing.

import net.corda.tools.shell.utlities.ANSIProgressRenderer;
import net.corda.tools.shell.utlities.CRaSHANSIProgressRenderer;
import org.crsh.cli.*;

import java.util.*;

public class StartShellCommand extends InteractiveShellCommand {
    @Command
    @Man("An alias for 'flow start'. Example: \"start Yo target: Some other company\"")
    public void main(final @Usage("The class name of the flow to run, or an unambiguous substring") @Argument String name,
                     final @Usage("The data to pass as input") @Argument(unquote = false) List<String> input) {
        ANSIProgressRenderer ansiProgressRenderer = ansiProgressRenderer();
        FlowShellCommand.startFlow(name, input, out, ops(), ansiProgressRenderer != null ? ansiProgressRenderer : new CRaSHANSIProgressRenderer(out), objectMapper());
    }
}

// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim.hubnet;

import org.nlogo.agent.Agent;
import org.nlogo.api.PerspectiveJ;
import org.nlogo.api.Syntax;
import org.nlogo.nvm.Context;

public strictfp class _hubnetsendwatch
    extends org.nlogo.nvm.Command {
  @Override
  public Syntax syntax() {
    return Syntax.commandSyntax
        (new int[]
            {Syntax.StringType(), Syntax.AgentType()},
            "OTPL", false);
  }

  @Override
  public void perform(final Context context) {
    final String client = argEvalString(context, 0);
    final Agent agent = argEvalAgent(context, 1);

    workspace.waitFor
        (new org.nlogo.api.CommandRunnable() {
          public void run() {
            workspace.getHubNetManager().sendAgentPerspective
                (client,
                 PerspectiveJ.WATCH().export(),
                 Agent.kindToClass(agent.kind()), agent.id, ((world.worldWidth() - 1) / 2), false);
          }
        });
    context.ip = next;
  }
}

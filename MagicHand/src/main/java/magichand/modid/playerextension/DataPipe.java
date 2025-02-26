package magichand.modid.playerextension;


import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.UUID;

/**
 * The Data Pipe is a class which acts as a bridge between the three classes PlayerRuntimeData, ManaManager &
 * CastMachine. It must fulfill the assertion, that every field is non-null!
 */
public class DataPipe {


    private boolean manaBurnout = false;
    private boolean loginRefreshRequired = true;


    public void setManaBurnoutState(boolean newState)
    {
        this.manaBurnout = newState;
    }

    public boolean getManaBurnoutState()
    {
        return manaBurnout;
    }





    /**
     * This method returns a boolean, wether the server must send a full refresh package because a player has just
     * logged on or not.
     * @return - Boolean: Login Refresh Required?
     */
    public boolean loginRefresh()
    {
        boolean loginRefresh = loginRefreshRequired;
        loginRefreshRequired = false;
        return loginRefresh;
    }

}

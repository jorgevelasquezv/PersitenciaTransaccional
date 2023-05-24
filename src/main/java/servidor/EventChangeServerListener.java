package servidor;

import java.util.EventListener;

public interface EventChangeServerListener extends EventListener {

    void onMessageChange(EventChangeServer evt);
}

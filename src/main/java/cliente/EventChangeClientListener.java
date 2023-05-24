package cliente;

import java.util.EventListener;

public abstract class EventChangeClientListener implements EventListener {

    void onMessageChange(EventChangeClient event){};

    void onDestiniesChange(EventChangeClient event){};


}

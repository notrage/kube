package kube.controller.network;

import kube.configuration.Config;
// Import kube classes
import kube.model.action.Action;
import kube.model.action.ActionType;
import kube.model.action.Queue;
import kube.services.Network;

// Import Java class
import java.io.IOException;

public class NetworkListener implements Runnable {

    /**********
     * ATTRIBUTES
     **********/

    Network network;
    Queue<Action> networkToModel;
    /**********
     * CONSTRUCTOR
     **********/

    /**
     * Constructor of the class
     * 
     * @param network        the network object
     * @param networkToModel the queue of actions to send to the model
     */
    public NetworkListener(Network network, Queue<Action> networkToModel) {
        this.network = network;
        this.networkToModel = networkToModel;
    }

    /**********
     * METHOD
     **********/

    @Override
    public void run() {
        while (true) {
            try {
                Action action = network.receive();
                Config.debug("Network received", action);
                if (action != null) {
                    action.setFromNetwork(true);
                    networkToModel.add(action);
                } else {
                    break;
                }
            } catch (IOException e) {
                Action action = new Action(ActionType.RESET);
                action.setFromNetwork(true);
                networkToModel.add(action);
                break;
            }
        }
    }
}

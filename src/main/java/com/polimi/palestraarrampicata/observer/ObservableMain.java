package com.polimi.palestraarrampicata.observer;

import java.util.ArrayList;
import java.util.List;

public class ObservableMain {

    private List<Observer> observerList = new ArrayList<>();


    public void addObserver (Observer obj){
        observerList.add(obj);
    }

    public  void  removeObserver(Observer obj){
        observerList.remove(obj);
    }

    public void notyObservers(){
        for(Observer obj:observerList){
            obj.update();
        }
    }

}

package com.polimi.palestraarrampicata.observer;

import com.polimi.palestraarrampicata.model.Utente;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EscursioneObservable {

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

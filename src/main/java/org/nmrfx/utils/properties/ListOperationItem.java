/*
 * NMRFx Processor : A Program for Processing NMR Data 
 * Copyright (C) 2004-2017 One Moon Scientific, Inc., Westfield, N.J., USA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nmrfx.utils.properties;

import java.util.ArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;

/**
 *
 * @author johnsonb
 */
public class ListOperationItem extends OperationItem implements ObservableObjectValue<String> {

    ArrayList value;
    ArrayList defaultValue;
    ChangeListener<? super String> listener;
    /**
     * This enables us to see the type of Unit that we interpret the List as.
     */
    ChoiceOperationItem typeSelector;

    ArrayList<String> listTypes;

    /**
     *
     * @param listener
     * @param defaultValue optional default value for the List.
     * @param listTypes The types of objects that will be contained in the list.
     * The List can only support one type of item at a time.
     * @param category
     * @param name
     * @param description
     * @param typeSelector
     */
    public ListOperationItem(ChangeListener listener, ArrayList defaultValue, ArrayList<String> listTypes, String category, String name, String description, ChoiceOperationItem typeSelector) {
        super(category, name, description);
        if (defaultValue != null) {
            this.defaultValue = defaultValue;
        } else {
            this.defaultValue = new ArrayList<>();
        }
        this.value = this.defaultValue;
        this.listener = listener;
        if (!(listTypes == null || listTypes.isEmpty())) {
            this.listTypes = listTypes;
        }
        this.typeSelector = typeSelector;
    }

    @Override
    public String getValue() {
        return value.toString();
    }

    public ArrayList getValueList() {
        return value;
    }

    @Override
    public Class<?> getType() {
        return ListOperationItem.class;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Value is set by giving a String of comma separated values. Based on the
     * listTypes, we will parse the String
     *
     * @param o
     */
    @Override
    public void setValue(Object o) {
        ArrayList oldValue = new ArrayList(value);
        ArrayList newValue;
        if (o instanceof String) {
            newValue = new ArrayList();
            String lst = (String) o;

            lst = lst.replace("[", "").replace("]", "");
            lst = lst.trim();

            if (lst.length() != 0) {
                for (String sValue : lst.split(",")) {
                    switch (sValue) {
                        case "":
                            newValue.add(0.0);
                            break;
                        case "-":
                            newValue.add(-0.0);
                            break;
                        default:
                            try {
                                newValue.add(Double.parseDouble(sValue));
                            } catch (NumberFormatException nfE) {
                                newValue.add(0.0);
                            }
                            break;
                    }
                }
            }
        } else if (o instanceof ArrayList) {
            newValue = new ArrayList((ArrayList) o);
        } else {
            return;
        }

        value = newValue;
        if (!value.equals(oldValue) && (listener != null)) {
            listener.changed(this, listToString(oldValue), listToString(value));
        }
    }

    @Override
    public String get() {
        return listToString(value);
    }

    private String listToString(ArrayList list) {
        StringBuilder str = new StringBuilder("");
        //String type = typeSelector.get();
        list.forEach((o) -> {
            if (o instanceof Number) {
                str.append(((Number) o).toString()).append(",");
            } else {
                System.out.println("non Number in List");
            }
        });
        //remove trailing comma
        return str.length() == 0 ? "" : str.toString().substring(0, str.toString().length() - 1);
    }

    @Override
    public void addListener(ChangeListener<? super String> listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener(ChangeListener<? super String> listener) {
    }

    @Override
    public void addListener(InvalidationListener listener) {
    }

    @Override
    public void removeListener(InvalidationListener listener) {
    }

    @Override
    public boolean isDefault() {
        return value.equals(defaultValue);
    }

    @Override
    public void setFromString(String sValue) {
        this.setValue(sValue);
    }

    @Override
    public void setToDefault() {
        value = defaultValue;
//        listener.changed(this, complexToString(old), complexToString(value));
    }

    @Override
    public String getStringRep() {
        return "[" + listToString(value) + "]";
    }
//    
//    @Override
//    public Optional<Class <? extends PropertyEditor>> getPropertyEditorClass() {
//        return ListPropertyEditor.class;
//    }

}

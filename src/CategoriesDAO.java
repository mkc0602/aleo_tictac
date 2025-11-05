/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.emmanet.model;

/*
 * #%L
 * InfraFrontier
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 EMBL-European Bioinformatics Institute
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;

/**
 *
 * @author phil
 */
public class CategoriesDAO implements Serializable {

    private int id_cat;
    private String last_change;
    private String main_cat;
    private String sub_cat;
    private String description;
    private String username;
    private String curated;

    /**
     * @return the id_cat
     */
    public int getId_cat() {
        return id_cat;
    }

    /**
     * @param id_cat the id_cat to set
     */
    public void setId_cat(int id_cat) {
        this.id_cat = id_cat;
    }

    /**
     * @return the last_change
     */
    public String getLast_change() {
        return last_change;
    }

    /**
     * @param last_change the last_change to set
     */
    public void setLast_change(String last_change) {
        this.last_change = last_change;
    }

    /**
     * @return the main_cat
     */
    public String getMain_cat() {
        return main_cat;
    }

    /**
     * @param main_cat the main_cat to set
     */
    public void setMain_cat(String main_cat) {
        this.main_cat = main_cat;
    }

    /**
     * @return the sub_cat
     */
    public String getSub_cat() {
        return sub_cat;
    }

    /**
     * @param sub_cat the sub_cat to set
     */
    public void setSub_cat(String sub_cat) {
        this.sub_cat = sub_cat;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurated() {
        return curated;
    }

    public void setCurated(String curated) {
        this.curated = curated;
    }

}

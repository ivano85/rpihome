/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.ivano85.rpihome.now.cards;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 *
 * @author ivano
 */
public class WeatherController {
    
    @FXML
    private Label city;
    @FXML
    private Label temperature;
    @FXML
    private Label minMaxTemperature;
    @FXML
    private Label wind;
    @FXML
    private Label humidity;
    @FXML
    private Label pressure;
    
    @FXML
    public void initialize() {
        
        Thread t = new Thread(new Updater(this));
        t.start();
        
    }
    
    public static class Updater implements Runnable {

        private final WeatherController controller;
        private final String key = "841a14bfdcb49c38ea4ce43b1c8824f6";
        private final int cityId = 3165524; // Turin

        public Updater(WeatherController controller) {
            this.controller = controller;
        }
        
        @Override
        public void run() {
            
            Gson gson = new GsonBuilder().create();
            
            while (true) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=" + cityId + "&APPID=" + key);
                    InputStream is = url.openStream();
                    Data data = gson.fromJson(new InputStreamReader(is, Charset.forName("UTF-8")), Data.class);
                    Platform.runLater(() -> {
                        controller.city.setText(data.getName() + ", " + data.getSys().getCountry());
                        controller.temperature.setText((data.getMain().get("temp") - 273.15) + " °C");
                        controller.minMaxTemperature.setText((data.getMain().get("temp_min") - 273.15) + " °C - " + (data.getMain().get("temp_max") - 273.15) + " °C");
                        //controller.wind.setText("Wind: " + data.getWind().get("speed").toString() + " - " + data.getWind().get("deg").toString() + "°");
                        controller.humidity.setText("Humidity: " + data.getMain().get("humidity").toString() + "%");
                        controller.pressure.setText("Pressure: " + data.getMain().get("pressure").toString());
                    });
                } catch (MalformedURLException ex) {
                    Logger.getLogger(WeatherController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(WeatherController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    Thread.sleep(1800000);
                } catch (InterruptedException ex) {
                    return;
                }
            }
            
        }
        
    }
    
    public static class Data {
        
        private Map<String, Double> coord;
        private List<Map<String, String>> wather;
        private String base;
        private Map<String, Double> main;
        private Map<String, Double> wind;
        private long dt;
        private DataSys sys;
        private long id;
        private String name;

        public Map<String, Double> getCoord() {
            return coord;
        }

        public void setCoord(Map<String, Double> coord) {
            this.coord = coord;
        }

        public List<Map<String, String>> getWather() {
            return wather;
        }

        public void setWather(List<Map<String, String>> wather) {
            this.wather = wather;
        }

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public Map<String, Double> getMain() {
            return main;
        }

        public void setMain(Map<String, Double> main) {
            this.main = main;
        }

        public Map<String, Double> getWind() {
            return wind;
        }

        public void setWind(Map<String, Double> wind) {
            this.wind = wind;
        }

        public long getDt() {
            return dt;
        }

        public void setDt(long dt) {
            this.dt = dt;
        }

        public DataSys getSys() {
            return sys;
        }

        public void setSys(DataSys sys) {
            this.sys = sys;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
    }
    
    public static class DataSys {
        
        private int type;
        private int id;
        private String country;
        private long sunrise;
        private long sunset;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public long getSunrise() {
            return sunrise;
        }

        public void setSunrise(long sunrise) {
            this.sunrise = sunrise;
        }

        public long getSunset() {
            return sunset;
        }

        public void setSunset(long sunset) {
            this.sunset = sunset;
        }
        
    }
    
}

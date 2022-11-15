import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiandq3
 * @date 2022/11/15
 */
public class ExcludePropertyPreFilterTest {
    
    @Test
    public void jackson_exclude_properties_test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept("password", "newPassword");
        FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);
        mapper.addMixIn(Map.class, DynamicFilter.class);
        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put("test", "123");
        jsonObject.put("password", "123456");
        jsonObject.put("newPassword", "18976");
        jsonObject.put("password1", "1232");
        String str = mapper.writer(filters).writeValueAsString(jsonObject);
        System.out.println(str);
    }
    
    @JsonFilter("myFilter")
    interface DynamicFilter {
    
    }
    
    @Test
    public void jackson_serialize_test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(Car.class, CarMix.class);
        Car audi = new Car("audi", 100000.0);
        String str = mapper.writeValueAsString(audi);
        System.out.println(str);
        Car car = mapper.readValue(str, Car.class);
        System.out.println(car);
    }
    
    static class CarMix {
        
        @JsonCreator
        public CarMix(@JsonProperty("branch") String branch, @JsonProperty("price") Double price) {
        
        }
    }
    
    static class Car {
        
        private String branch;
        
        private Double price;
        
        public Car(String branch, Double price) {
            this.branch = branch;
            this.price = price;
        }
        
        public String getBranch() {
            return branch;
        }
        
        public void setBranch(String branch) {
            this.branch = branch;
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(Double price) {
            this.price = price;
        }
        
        @Override
        public String toString() {
            return "Car{" + "branch='" + branch + '\'' + ", price=" + price + '}';
        }
    }
    
}

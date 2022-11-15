import cn.chien.utils.AddressUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qiandq3
 * @date 2022/11/15
 */
public class AddressUtilsTest {
    
    @Test
    public void getRealAddressByIP_test() {
        String ip = "14.215.177.38";
        String address = AddressUtils.getRealAddressByIP(ip);
        Assert.assertEquals("广东省 广州市", address);
    }
    
}

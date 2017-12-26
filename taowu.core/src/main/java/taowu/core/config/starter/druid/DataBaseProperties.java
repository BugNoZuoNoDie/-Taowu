package taowu.core.config.starter.druid;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = DataBaseProperties.PREFIX)
public class DataBaseProperties {
	
	public static final String PREFIX = "DB";
	
	/**
	 * db名字
	 */
	String dbName;
	
	/**
	 * 数据源
	 */
	List<Datas> datas = new ArrayList<>();
	
	/**
     * 是否启用
     */
    boolean enabled=true;
	
	public List<Datas> getDatas() {
		return datas;
	}

	public void setDatas(List<Datas> datas) {
		this.datas = datas;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public static class Datas{
		String url;
		String username;
		String pwd;
		String driverClassName;
		ProducerType producerType=ProducerType.TEST;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPwd() {
			return pwd;
		}
		public void setPwd(String pwd) {
			this.pwd = pwd;
		}
		public String getDriverClassName() {
			return driverClassName;
		}
		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}
		public ProducerType getProducerType() {
			return producerType;
		}
		public void setProducerType(ProducerType producerType) {
			this.producerType = producerType;
		}
	}
	
	public enum ProducerType{
		TEST("测试","test");
		/**
         * 枚举值
         */
        private String value;
        /**
         * 描述
         */
        private String desc;

        private ProducerType(String desc, String value) {
            this.value = value;
            this.desc = desc;
        }
	}
	
}

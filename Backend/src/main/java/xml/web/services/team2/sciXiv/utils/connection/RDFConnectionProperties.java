package xml.web.services.team2.sciXiv.utils.connection;

import java.util.Properties;

public class RDFConnectionProperties {

    private String endpoint;
    private String dataset;
    private String queryEndpoint;
    private String updateEndpoint;
    private String dataEndpoint;

    public RDFConnectionProperties() {}

    public RDFConnectionProperties(Properties props) {
        dataset = props.getProperty("conn.dataset").trim();
        endpoint = props.getProperty("conn.endpoint").trim();

        queryEndpoint = String.join("/", endpoint, dataset, props.getProperty("conn.query").trim());
        updateEndpoint = String.join("/", endpoint, dataset, props.getProperty("conn.update").trim());
        dataEndpoint = String.join("/", endpoint, dataset, props.getProperty("conn.data").trim());
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getQueryEndpoint() {
        return queryEndpoint;
    }

    public void setQueryEndpoint(String queryEndpoint) {
        this.queryEndpoint = queryEndpoint;
    }

    public String getUpdateEndpoint() {
        return updateEndpoint;
    }

    public void setUpdateEndpoint(String updateEndpoint) {
        this.updateEndpoint = updateEndpoint;
    }

    public String getDataEndpoint() {
        return dataEndpoint;
    }

    public void setDataEndpoint(String dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }
}

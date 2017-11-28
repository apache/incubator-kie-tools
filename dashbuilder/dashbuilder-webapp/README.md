Dashbuilder web application
============================

System metrics dashboard
------------------------

This web application provides an example dashboard for monitoring real time system metrics.            

The dashboard is called **System metrics** and can be found in the tree menu *combined* of the gallery perspective.                

In order to run this example you must enable and configure the <code>collectd</code> daemon (Unix/Linux systems) as:                     

1.- Install <code>collectd</code> package         
    
        sudo yum install collectd
        
2.- Configure <code>/etc/collectd.conf</code> as the following example:

        Hostname    "host.example.com"
        Interval     1
        LoadPlugin memory
        LoadPlugin csv
        <Plugin csv>
            # Use a custom data dir location.
        	DataDir	"/tmp/metrics/csv"
        	StoreRates false
        </Plugin>
        
3.- Start the service                 

    service collectd start

4.- Check service is up             

    service collectd status

5.- Create a new data set definition or modify the <code>filePath</code> value of existing file located at [here](./src/main/webapp/datasets/metrics_csv.dset) as:            

    {
        "uuid": "metrics_csv",
        "provider": "CSV",
        "isPublic": true,
        "refreshTime": "1second",
        "refreshAlways": "true",
        "filePath": "/tmp/metrics/csv/host.example.com/memory/memory-used-2015-02-04",
        "separatorChar": ",",
        "quoteChar": "\"",
        "escapeChar": "\\",
        "datePattern": "epoch",
        "numberPattern": "#.##",
        "columns": [
          {"id": "epoch", "type": "date", "pattern": "epoch"},
          {"id": "value", "type": "number", "pattern": "#.##"}
        ]
    }

6.- Run the web application and navigate to *System metrics* dashboard                

#TODO: Use of elasticseach provider instead of the csv one.

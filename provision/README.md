# Criar o seguinte indice no ElasticSearch 5.x. "Mappings" não são suportados no ElasticSearch >= 6.x

```
PUT /provision
{
  "mappings": {
    "provider": {
    	"properties": {
    		"id" : { "index": false }	
    	}
    },
    "datacenter": {
    	"properties": {
    		"id" : { "index": false }	
    	}
    },
    "cluster": {
    	"properties": {
    		"id" : { "index": false }	
    	}
    },
    "server": {
    	"properties": {
    		"id" : { "index": false }	
    	}
    }
  }
}
```

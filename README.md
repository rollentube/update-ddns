# update-ddns
Command line tool to update a dynamic DNS entry to your current public IP over a web API.

Configured for entries by the german provider [Securepoint](http://spdyn.de/).

### Usage:
```
usage: update-ddns --dns-name <DNS-ENTRY> [-h] --token <TOKEN> [-v]
    --dns-name <DNS-ENTRY>   Dynamic dns entry to change
 -h,--help                   Print this help text
    --token <TOKEN>          API token to change the entry
 -v,--verbose                Show verbose information [NOT IMPLEMENTED]
 ```

# update-ddns
Command line tool to update a dynamic DNS entry to your current public IP over a web API.

Configured for entries by the german provider [Securepoint](http://spdyn.de/).

### Usage
```
usage: update-ddns --dns-name <DNS-ENTRY> [-h] --token <TOKEN> [-v]
    --dns-name <DNS-ENTRY>   Dynamic dns entry to change
 -h,--help                   Print this help text
    --token <TOKEN>          API token to change the entry
 -v,--verbose                Show verbose information [NOT IMPLEMENTED]
 ```

### Disclaimer
I am a cyber security student, who is starting with Java and want to learn more and more. So i know my work here is not perfect or maybe even good. It is just for research purposes. Probably it is also better to program such a commandline application in a different language. I already wrote it in Powershell to run it on my Windows machine. I appreciate any kind of feedback or suggestions for improvement.

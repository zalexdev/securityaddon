# Security addon - 4.0

This app made for emergency case. It can delete/clear selected apps, delete files and folder and crypt files with AES-256 and your password. It can be launched from fake shortcut and sms! 

## List of features



 Delete apps
- Root and system apps supports
- Clear app data
- Silent delete with root

Delete files and folders
- Silent without root

Crypt with aes-256
- Set own non 16 character password(app will autogenerate to 16)
- Crypt all files inside folder

SMS
- Set code word or phrase
- Set trusted numbers
Shortcut
- Set own icon
- Set own name
- Set what app open on click


## Own triggers supported!
Command line

```
am start -n com.huntmix.secbutton/.Deleter --activity-no-history
```
From your app
```
Intent intent = new Intent();
        intent.setClassName("com.huntmix.secbutton","com.huntmix.secbutton.Backgroundstarter");
        startService(intent);
```
With this you can run app from taskers, smart watches, voice commands and other!
## Known bugs
Issue is welcome! Leave here bugs, wishes, improvements and ideas!
* Not crypting files in multifolder (like folder in folder)
* Some UI bugs with small screens
## Contributing
If you make addon for my app, integration or modifying:
* Leave link to this [github page](https://github.com/huntmix/securityaddon) 
* Leave [coffe](https://huntmix.ru/rekv.html) link for donations
* Your app must be opensource (if not - contact me)


## License
[AGPL V3](https://www.gnu.org/licenses/agpl-3.0.ru.html)

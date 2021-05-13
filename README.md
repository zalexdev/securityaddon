# Security addon - 6.1

This app made for emergency case. It can delete/clear selected apps, delete files and folder and crypt files with AES-256 and your password. It can be launched from fake shortcut and sms! It has own powerful API wich anybody can use!
> This version was special maded for Demhack 2!
# Changelog
- Code rewrited
- New design
- Added Android 11 support
- Added take photo
- Added integration with any app by notification
- Added Select what actions to start per trigger
- Added small devices and landscape mode support
- Many fixes and imporvments
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
- Send SOS sms for selected contacts

Shortcut
- Set own icon
- Set own name
- Set what app open on click

Special
- Detecting wrong pin entered
- Take photo
- Integration with any app by notification

## API
When we need to use it?
> If you app supported other trigers, need crypt files before send or you want to make addon - you are welcome!

What can api do?
- Delete files
- Crypt files/folders
- Decrypt files/folders

How to?
3 modes:
> delete, crypt, decrypt

You need send to api wth extras:  mode, path to folder or files(s) (as arraylist) and for last 2 modes - password.
Example:
``` Java
//List with pass
public ArrayList<String> path = new ArrayList<String>();
path.add("/storage/emulated/0/DCIM/Camera/"); //adding files or folders
Intent intent = new Intent();
        intent.setClassName("com.huntmix.secbutton","com.huntmix.secbutton.ApiCall");
        intent.putExtra("mode","decrypt");
        intent.putExtra("pass","1111111111111111");
        intent.putStringArrayListExtra("path",path);
        startActivity(intent); //launching
```
How it works you can see in Api.java
# Own triggers
Command line

``` Bash
am start -n com.huntmix.secbutton/.Deleter --activity-no-history
```
From your app
``` Java
Intent intent = new Intent();
        intent.setClassName("com.huntmix.secbutton","com.huntmix.secbutton.Deleter");
        startActivity(intent);
```
With this you can run app from taskers, smart watches, voice commands and other!
## Known bugs
Issue is welcome! Leave here bugs, wishes, improvements and ideas!
* Not crypting files in multifolder (like folder in folder)
- You tell me
## Contributing
If you make addon for my app, integration or modifying:
* Leave link to this [github page](https://github.com/huntmix/securityaddon) 
* Leave [coffe](https://huntmix.ru/donation.html) link for donations
* Your app must be opensource (if not - contact me)


## License
[AGPL V3](https://www.gnu.org/licenses/agpl-3.0.ru.html)

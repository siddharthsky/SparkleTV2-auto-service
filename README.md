# Sparkle-TV2

This Android app serves as an opener for the IPTV player installed by the user after running Termux.

## Overview

The app is designed to check if a server is running successfully on Termux at `localhost:5001`. If the server is running, the app opens the user's installed IPTV player.

## Features

- Checks for the running server on Termux.
- Opens the installed IPTV player if the server is running.
- Simplifies the process of launching the IPTV player after setting up the server on Termux.
- If the server is already running, it just opens the configured IPTV player.

## How to Use

1. Ensure you have [Termux](https://github.com/termux/termux-app) installed on your Android device.
2. Configure Termux autostart via `bash.bashrc`:
    - Open Termux.
    - Edit `bash.bashrc` using a text editor like nano:
   
        ```bash
        nano $PREFIX/etc/bash.bashrc
        ```
    - Add the command to start your JioTV server (e.g., `./jiotv_go.sh run ":5001"` - for JioTV GO server) at the end of the file.
    - Server running at "localhost:5001" are supported.
    - Save and exit.

3. Select the installed IPTV player app on your device.

4. Run the app.

5. If the server is running successfully, the app will open the installed IPTV player.

6. If you want to choose another app, clear the app data to reset.

## Dependencies

- Termux (for running the server)
- Server configured with autostart
  - JioTV GO server by [rabilrbl](https://github.com/rabilrbl/jiotv_go) ([recommended](https://rabilrbl.github.io/jiotv_go/Usage-Guide/#android-users-weve-got-you-covered))
  - J-TV self server NPM by [dhruv-2015](https://github.com/dhruv-2015/JIOTVServer)
  - TS-JioTV server NPM by [mitthu786](https://github.com/mitthu786/TS-JioTV)
- IPTV player with playlist [playstore](https://play.google.com/store/search?q=iptv+player&c=apps)


## Acknowledgements

- [Termux](https://github.com/termux) 
- [rabilrbl](https://github.com/rabilrbl)
- [dhruv-2015](https://github.com/dhruv-2015)
- [mitthu786](https://github.com/mitthu786)

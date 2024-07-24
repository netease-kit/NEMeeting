const { app, BrowserWindow, ipcMain } = require('electron')
const path = require('node:path')
const { default: NEMeetingKit } = require('nemeeting-electron-sdk')

const createWindow = () => {
  // Create the browser window.
  const mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      contextIsolation: false,
      nodeIntegration: true,
      enableRemoteModule: true,
      preload: path.join(__dirname, './preload.js'),
    },
  })

  // and load the index.html of the app.
  mainWindow.loadFile(path.join(__dirname, 'index.html'))

  // Open the DevTools.
  mainWindow.webContents.openDevTools()

  ipcMain.handle('initialize', (_, value) => {
    const neMeetingKit = NEMeetingKit.getInstance()
    const appKey = value.appKey

    return neMeetingKit.initialize({
      appKey,
      serverUrl: 'https://meeting.yunxinroom.com/'
    })
  })

  ipcMain.handle('loginByPassword', async (_, value) => {
    //使用 账号密码 登录
    const neMeetingKit = NEMeetingKit.getInstance()
    const accountService = neMeetingKit.getAccountService()
    // 用户账户
    const userUuid = value.userUuid
    // 用户密码
    const password = value.password

    try {
      const res = await accountService.loginByPassword(userUuid, password)

      return res
    } catch (error) {
      return error
    }
  })

  ipcMain.handle('startMeeting', async (_, value) => {
    //使用 账号密码 登录
    const neMeetingKit = NEMeetingKit.getInstance()
    const meetingService = neMeetingKit.getMeetingService()

    const param = {
      displayName: value.displayName,
    }

    try {
      const res = await meetingService.startMeeting(param)

      return res
    } catch (error) {
      return error
    }
  })
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
  createWindow()

  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and import them here.

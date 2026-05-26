# ─────────────────────────────────────────────────────────────────────────────
# economy.sk — Full Diamond/Coin Economy System with Toggle & Test Mode
# Required addons: Skript, SkriptFastBoard (or compatible), skript-miniMessage, Skript-Math
# ─────────────────────────────────────────────────────────────────────────────

options:
    money_var: money::*
    prefix: &8[&6Economy&8] &r
    coin_name: &6Coin
    webhook: https://discord.com/api/webhooks/1508923837176090817/1uSTcZpDsg6ex2JkqvDQ78_40Dg4QIQI9WaQrmRKxbXT1iZvr25vcGJ-seFST8Qa7F1s

# ─── Load: init toggles + baltop ──────────────────────────────────────────────

on load:
    if {economy::toggle::scoreboard} is not set:
        set {economy::toggle::scoreboard} to true
    if {economy::toggle::pay} is not set:
        set {economy::toggle::pay} to true
    if {economy::toggle::deposit} is not set:
        set {economy::toggle::deposit} to true
    if {economy::toggle::withdraw} is not set:
        set {economy::toggle::withdraw} to true
    if {economy::toggle::balance} is not set:
        set {economy::toggle::balance} to true
    if {economy::testmode} is not set:
        set {economy::testmode} to false
    update_baltop()

# ─── Join: init balance + scoreboard loop ─────────────────────────────────────

on join:
    if {money::%player's uuid%} is not set:
        set {money::%player's uuid%} to 0
    while player is online:
        # Decide whether to show the board
        set {_show} to false
        if player is op:
            set {_show} to true
        else:
            if {economy::toggle::scoreboard} is true:
                if {economy::testmode} is false:
                    set {_show} to true

        if {_show} is true:
            set title of player's fastboard to minimessage from "<#ffe857>ECONOMY"
            set line 1 of player's fastboard to minimessage from "<dark_gray>──────"
            set line 2 of player's fastboard to minimessage from " <gray>TOP 3"
            set line 3 of player's fastboard to minimessage from " <#ffe857>🥇 <white>%{baltop::1}%"
            set line 4 of player's fastboard to minimessage from " <#d2e9ff>🥈 <white>%{baltop::2}%"
            set line 5 of player's fastboard to minimessage from " <#ff8c57>🥉 <white>%{baltop::3}%"
            set line 6 of player's fastboard to minimessage from "<dark_gray>──────"
            set line 7 of player's fastboard to minimessage from " <gray>Balance"
            set line 8 of player's fastboard to minimessage from " <#ffe857>⬡ <white>%{money::%player's uuid%}%"
            # Status indicator (line 9) — only meaningful for OPs
            if player is op:
                if {economy::testmode} is true:
                    set line 9 of player's fastboard to minimessage from " <red>⚠ <gray>Test Mode"
                else if {economy::toggle::scoreboard} is false:
                    set line 9 of player's fastboard to minimessage from " <yellow>⚠ <gray>Hidden"
                else:
                    set line 9 of player's fastboard to minimessage from "<dark_gray>──────"
            else:
                set line 9 of player's fastboard to minimessage from "<dark_gray>──────"
        else:
            set title of player's fastboard to minimessage from " "
            set line 1 of player's fastboard to minimessage from " "
            set line 2 of player's fastboard to minimessage from "  "
            set line 3 of player's fastboard to minimessage from "   "
            set line 4 of player's fastboard to minimessage from "    "
            set line 5 of player's fastboard to minimessage from "     "
            set line 6 of player's fastboard to minimessage from "      "
            set line 7 of player's fastboard to minimessage from "       "
            set line 8 of player's fastboard to minimessage from "        "
            set line 9 of player's fastboard to minimessage from "         "
        wait 1 second

# ─── /econtoggle ──────────────────────────────────────────────────────────────

command /econtoggle [<string>]:
    permission: op
    trigger:
        if arg-1 is not set:
            set {_sb} to statusColor({economy::toggle::scoreboard})
            set {_pay} to statusColor({economy::toggle::pay})
            set {_dep} to statusColor({economy::toggle::deposit})
            set {_wd} to statusColor({economy::toggle::withdraw})
            set {_bal} to statusColor({economy::toggle::balance})
            set {_tm} to statusColor({economy::testmode})
            send "{@prefix}&e&lToggle Status" to player
            send "  &7Scoreboard: %{_sb}%" to player
            send "  &7/pay: %{_pay}%" to player
            send "  &7Deposit: %{_dep}%" to player
            send "  &7Withdraw: %{_wd}%" to player
            send "  &7/balance: %{_bal}%" to player
            send "  &7Test Mode (&e/econopen&7): %{_tm}%" to player
            send "&8Usage: &7/econtoggle &e<scoreboard|pay|deposit|withdraw|balance|all>" to player
            stop
        if arg-1 = "scoreboard":
            flipToggle("scoreboard")
            set {_s} to statusColor({economy::toggle::scoreboard})
            send "{@prefix}&7Scoreboard → %{_s}%" to player
        else if arg-1 = "pay":
            flipToggle("pay")
            set {_s} to statusColor({economy::toggle::pay})
            send "{@prefix}&7/pay → %{_s}%" to player
        else if arg-1 = "deposit":
            flipToggle("deposit")
            set {_s} to statusColor({economy::toggle::deposit})
            send "{@prefix}&7Deposit → %{_s}%" to player
        else if arg-1 = "withdraw":
            flipToggle("withdraw")
            set {_s} to statusColor({economy::toggle::withdraw})
            send "{@prefix}&7Withdraw → %{_s}%" to player
        else if arg-1 = "balance":
            flipToggle("balance")
            set {_s} to statusColor({economy::toggle::balance})
            send "{@prefix}&7/balance → %{_s}%" to player
        else if arg-1 = "all":
            if {economy::toggle::scoreboard} is true:
                set {economy::toggle::scoreboard} to false
                set {economy::toggle::pay} to false
                set {economy::toggle::deposit} to false
                set {economy::toggle::withdraw} to false
                set {economy::toggle::balance} to false
                send "{@prefix}&cAll features &4OFF &7(OPs still bypass)" to player
            else:
                set {economy::toggle::scoreboard} to true
                set {economy::toggle::pay} to true
                set {economy::toggle::deposit} to true
                set {economy::toggle::withdraw} to true
                set {economy::toggle::balance} to true
                send "{@prefix}&aAll features &2ON" to player
        else:
            send "{@prefix}&cUnknown option! Use: &escoreboard, pay, deposit, withdraw, balance, all" to player

on tab complete of "/econtoggle":
    set tab completions for position 1 to "scoreboard", "pay", "deposit", "withdraw", "balance" and "all"

function statusColor(b: boolean) :: string:
    if {_b} is true:
        return "&aON"
    else:
        return "&cOFF"

function flipToggle(name: string):
    if {economy::toggle::%{_name}%} is true:
        set {economy::toggle::%{_name}%} to false
    else:
        set {economy::toggle::%{_name}%} to true

# ─── Discord Webhook (disabled) ───────────────────────────────────────────────
# SkHttp v1.5 is broken on Skript 2.15.x — leaving this as a no-op until a working
# HTTP addon is available. All sendWebhook(...) calls below are kept commented out.

function sendWebhook(msg: string):
    # no-op
    return

# ─── /econopen — Private OP test mode ─────────────────────────────────────────
# When ON: economy hidden + disabled for everyone EXCEPT ops.
# OPs see/use everything normally with a "⚠ Test Mode ON" indicator.

command /econopen:
    permission: op
    trigger:
        if {economy::testmode} is true:
            set {economy::testmode} to false
            send "{@prefix}&aTest mode &2disabled&a. Economy is live for everyone." to player
            broadcast "{@prefix}&aThe economy is now &2online&a!"
        else:
            set {economy::testmode} to true
            send "{@prefix}&eTest mode &6enabled&e. Only you (OPs) can see/use the economy." to player
            loop all players:
                if loop-player is not op:
                    send "{@prefix}&cThe economy is currently &4under maintenance&c. Check back soon!" to loop-player

# ─── Baltop ───────────────────────────────────────────────────────────────────

every 1 minute:
    update_baltop()

command /updatebaltop:
    permission: op
    trigger:
        set {_N} to now
        send "{@prefix}&aUpdating baltop..." to player
        update_baltop()
        send "{@prefix}&aFinished! &7(%difference between now and {_N}%)" to player

function update_baltop():
    set {baltop::*} to getBaltop(3)

function getBaltop(i: integer) :: strings:
    set {_I} to clamp({_I}, 1, 10)
    loop {_I} times:
        set {_T::%loop-iteration%} to 0
        set {_N::%loop-iteration%} to ""
    loop {money::*}:
        continue if loop-value = 0
        loop {_I} times:
            loop-value-1 > {_T::%loop-iteration-2%}
            set {_A::*} to {_T::*}
            set {_B::*} to {_N::*}
            set {_T::%loop-iteration-2%} to loop-value-1
            set {_N::%loop-iteration-2%} to loop-index-1
            loop {_I} - loop-iteration-2 times:
                set {_T::%{_I} - loop-iteration-3 + 1%} to {_A::%{_I} - loop-iteration-3%}
                set {_N::%{_I} - loop-iteration-3 + 1%} to {_B::%{_I} - loop-iteration-3%}
            exit 1 loop
    loop {_I} times:
        if {_N::%loop-iteration%} = "":
            set {_F::%loop-iteration%} to "-"
        else:
            set {_F::%loop-iteration%} to "%{_N::%loop-iteration%} parsed as offlineplayer's name% <gray>%{_T::%loop-iteration%}%"
    return {_F::*}

command /baltop:
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThe economy is currently under maintenance." to player
                stop
        send "{@prefix}&e&lTop 3 Richest Players:" to player
        loop 3 times:
            send "  &6#%loop-number% &e%{baltop::%loop-number%}%" to player

# ─── Economy Admin ────────────────────────────────────────────────────────────

command /economy [<string>] [<offlineplayer>] [<integer>]:
    permission: op
    trigger:
        if arg-1 is not set:
            send "{@prefix}&cUsage: /economy <set/add/remove/reset> <player> [amount]" to player
            stop
        if arg-2 is not set:
            send "{@prefix}&cUsage: /economy <set/add/remove/reset> <player> [amount]" to player
            stop
        if arg-1 = "reset":
            set {money::%arg-2's uuid%} to 0
            send "{@prefix}&aReset &2%arg-2%'s &abalance to &20 coins&a!" to player
            sendWebhook("⚙️ **%player%** RESET **%arg-2%**'s balance to 0")
            stop
        if arg-3 is not set:
            send "{@prefix}&cPlease provide an amount!" to player
            stop
        if arg-1 = "set":
            set {money::%arg-2's uuid%} to arg-3
            send "{@prefix}&aSet &2%arg-2%'s &abalance to &2%arg-3% coins&a!" to player
            sendWebhook("⚙️ **%player%** SET **%arg-2%**'s balance to %arg-3%")
        else if arg-1 = "add":
            add arg-3 to {money::%arg-2's uuid%}
            send "{@prefix}&aAdded &2%arg-3% coins &ato &2%arg-2%&a!" to player
            sendWebhook("⚙️ **%player%** ADDED %arg-3% coins to **%arg-2%** | Balance: %{money::%arg-2's uuid%}%")
        else if arg-1 = "remove":
            if arg-3 > {money::%arg-2's uuid%}:
                send "{@prefix}&c%arg-2% &cdoesn't have enough coins to remove!" to player
                stop
            remove arg-3 from {money::%arg-2's uuid%}
            send "{@prefix}&aRemoved &2%arg-3% coins &afrom &2%arg-2%&a!" to player
            sendWebhook("⚙️ **%player%** REMOVED %arg-3% coins from **%arg-2%** | Balance: %{money::%arg-2's uuid%}%")
        else:
            send "{@prefix}&cUnknown sub-command! Use: &eset, add, remove, reset" to player

on tab complete of "/economy":
    set tab completions for position 1 to "set", "add", "remove" and "reset"

# ─── /balance ─────────────────────────────────────────────────────────────────

command /balance [<offlineplayer>]:
    aliases: /bal
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
            if {economy::toggle::balance} is false:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
        set {_P} to arg-1 ? player
        send "{@prefix}&3%{_P}%'s Balance: &b%{money::%{_P}'s uuid%}% coins" to player

# ─── /pay ─────────────────────────────────────────────────────────────────────

command /pay [<player>] [<integer>]:
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
            if {economy::toggle::pay} is false:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
        if arg-1 is not set:
            send "{@prefix}&cUsage: /pay <player> <amount>" to player
            stop
        if arg-2 is not set:
            send "{@prefix}&cUsage: /pay <player> <amount>" to player
            stop
        if arg-1 = player:
            send "{@prefix}&cYou can't pay yourself!" to player
            stop
        if arg-2 <= 0:
            send "{@prefix}&cAmount must be greater than 0!" to player
            stop
        if arg-2 > {money::%player's uuid%}:
            send "{@prefix}&cNot enough coins! Balance: &e%{money::%player's uuid%}% coins&c." to player
            stop
        remove arg-2 from {money::%player's uuid%}
        add arg-2 to {money::%arg-1's uuid%}
        send "{@prefix}&aPaid &2%arg-2% coins &ato &2%arg-1%&a!" to player
        send "{@prefix}&2%player% &apaid you &2%arg-2% coins&a!" to arg-1
        sendWebhook("💸 **%player%** paid **%arg-2%** coins to **%arg-1%** | %player%'s balance: %{money::%player's uuid%}% | %arg-1%'s balance: %{money::%arg-1's uuid%}%")

# ─── /depositdiamond ──────────────────────────────────────────────────────────

command /depositdiamond [<integer>]:
    aliases: /depositdiamonds
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
            if {economy::toggle::deposit} is false:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
        if arg-1 is not set:
            send "{@prefix}&cUsage: /depositdiamond <amount>" to player
            stop
        if arg-1 <= 0:
            send "{@prefix}&cAmount must be greater than 0!" to player
            stop
        set {_has} to amount of diamond in player's inventory
        if arg-1 > {_has}:
            send "{@prefix}&cNot enough diamonds! You have &e%{_has}% diamonds&c." to player
            stop
        remove arg-1 of diamond from player's inventory
        add arg-1 to {money::%player's uuid%}
        send "{@prefix}&aDeposited &e%arg-1% diamonds &a→ &e%arg-1% coins&a! Balance: &e%{money::%player's uuid%}% coins&a." to player
        sendWebhook("💎➡️🪙 **%player%** deposited **%arg-1%** diamonds → coins | Balance: %{money::%player's uuid%}%")

# ─── /withdrawdiamond ─────────────────────────────────────────────────────────

command /withdrawdiamond [<integer>]:
    aliases: /withdrawdiamonds
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
            if {economy::toggle::withdraw} is false:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
        if arg-1 is not set:
            send "{@prefix}&cUsage: /withdrawdiamond <amount>" to player
            stop
        if arg-1 <= 0:
            send "{@prefix}&cAmount must be greater than 0!" to player
            stop
        if arg-1 > {money::%player's uuid%}:
            send "{@prefix}&cNot enough coins! Balance: &e%{money::%player's uuid%}% coins&c." to player
            stop
        remove arg-1 from {money::%player's uuid%}
        give arg-1 of diamond to player
        send "{@prefix}&aWithdrew &e%arg-1% coins &a→ &e%arg-1% diamonds&a! Balance: &e%{money::%player's uuid%}% coins&a." to player
        sendWebhook("🪙➡️💎 **%player%** withdrew **%arg-1%** coins → diamonds | Balance: %{money::%player's uuid%}%")

# ─── /depositcoins ────────────────────────────────────────────────────────────

command /depositcoins [<integer>]:
    aliases: /depositcoin
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
            if {economy::toggle::deposit} is false:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
        if arg-1 is not set:
            send "{@prefix}&cUsage: /depositcoins <amount>" to player
            stop
        if arg-1 <= 0:
            send "{@prefix}&cAmount must be greater than 0!" to player
            stop
        set {_has} to amount of (gold nugget named "{@coin_name}") in player's inventory
        if arg-1 > {_has}:
            send "{@prefix}&cNot enough coin items! You have &e%{_has}%&c." to player
            stop
        remove arg-1 of (gold nugget named "{@coin_name}") from player's inventory
        add arg-1 to {money::%player's uuid%}
        send "{@prefix}&aDeposited &e%arg-1% coin items &ato your balance! Balance: &e%{money::%player's uuid%}% coins&a." to player
        sendWebhook("🪙📥 **%player%** deposited **%arg-1%** coin items | Balance: %{money::%player's uuid%}%")

# ─── /withdrawcoins ───────────────────────────────────────────────────────────

command /withdrawcoins [<integer>]:
    aliases: /withdrawcoin
    trigger:
        if player is not op:
            if {economy::testmode} is true:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
            if {economy::toggle::withdraw} is false:
                send "{@prefix}&cThis feature is currently disabled." to player
                stop
        if arg-1 is not set:
            send "{@prefix}&cUsage: /withdrawcoins <amount>" to player
            stop
        if arg-1 <= 0:
            send "{@prefix}&cAmount must be greater than 0!" to player
            stop
        if arg-1 > {money::%player's uuid%}:
            send "{@prefix}&cNot enough coins! Balance: &e%{money::%player's uuid%}% coins&c." to player
            stop
        remove arg-1 from {money::%player's uuid%}
        give arg-1 of (gold nugget named "{@coin_name}") to player
        send "{@prefix}&aWithdrew &e%arg-1% coins &aas coin items! Balance: &e%{money::%player's uuid%}% coins&a." to player
        sendWebhook("🪙📤 **%player%** withdrew **%arg-1%** coins as items | Balance: %{money::%player's uuid%}%")

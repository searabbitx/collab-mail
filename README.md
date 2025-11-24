# CollabMail - Temporary Mail via Burp Collaborator

A Burp Suite extension that lets you generate temporary email addresses directly from within Burp. It uses **Burp Collaborator** to capture incoming emails and allows you to see the full SMTP conversation along plain-text, raw html and rendered html versions.

Requires Burp Pro

![[]](preview.png)

## NOTE: If you are using a public collaborator server

PortSwigger [does not recommend](https://portswigger.net/burp/documentation/collaborator/server/security#collaborator-based-email-addresses) registering for websited using a Collaborator-based email address if you are using the public collaborator server due to a potential for the messages to be leaked. Also it seems that the public server uses the 8192 byte limit on SMTP conversation length, so if you expect to receive long messages (for example containing attachments), deploy your own collaborator server and set the [interactionLimits field](https://portswigger.net/burp/documentation/collaborator/server/private/example#:~:text=The%20maximum%20number%20of%20bytes%20that%20are%20stored%20for%20each%20incoming%20SMTP%20interaction) as needed.

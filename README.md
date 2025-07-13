<img src="./.github/assets/app-icon.png" alt="Voice Assistant App Icon" width="100" height="100">

# Friday AI - Intelligent Voice Assistant

This is a Friday AI voice assistant inspired by Iron Man's Friday AI, built with [LiveKit Agents](https://docs.livekit.io/agents/overview/) and the [LiveKit Android SDK](https://github.com/livekit/client-sdk-android).

Friday AI features:
- 🎭 **Personality & Humor**: Witty, intelligent, and supportive just like Tony Stark's Friday
- 🎨 **Friday AI Theme**: Futuristic cyan/blue UI inspired by Iron Man's technology
- 🧠 **Intelligence**: Smart task handling and proactive assistance
- 🎤 **Female Voice**: Professional yet friendly female voice
- 💫 **Flawless Experience**: Error-free, smooth interactions

This app is ready to use and free for you to modify as you see fit.

## Getting started

The easiest way to get this app running is with the [Sandbox for LiveKit Cloud](https://cloud.livekit.io/projects/p_/sandbox) and the [LiveKit CLI](https://docs.livekit.io/home/cli/cli-setup/).

First, create a new [Sandbox Token Server](https://cloud.livekit.io/projects/p_mytc7vpzfkt/sandbox/templates/token-server) for your LiveKit Cloud project.

Then, run the following command to automatically clone this template and connect it to LiveKit Cloud:

```bash
lk app create --template android-voice-assistant --sandbox <token_server_sandbox_id>
```

Build and run the app in Android Studio.

You'll also need an agent to speak with. For the full Friday AI experience with personality and humor, check out the `FRIDAY_AI_PERSONALITY_GUIDE.md` file which explains how to implement Friday's personality in the backend agent. You can also try our sample voice assistant agent for [Python](https://github.com/livekit-examples/voice-pipeline-agent-python), [Node.js](https://github.com/livekit-examples/voice-pipeline-agent-node), or [create your own from scratch](https://docs.livekit.io/agents/quickstart/).

> [!NOTE]
> To setup without the LiveKit CLI, clone the repository and edit the `TokenExt.kt` file to add either a `sandboxID` (if using a [Sandbox Token Server](https://cloud.livekit.io/projects/p_/sandbox/templates/token-server)), or a [manually generated](#token-generation) URL and token.

## Token generation

In a production environment, you will be responsible for developing a solution to [generate tokens for your users](https://docs.livekit.io/home/server/generating-tokens/) which is integrated with your authentication solution. You should disable your sandbox token server and modify `TokenExt.kt` to use your own token server.

## Contributing

This template is open source and we welcome contributions! Please open a PR or issue through GitHub, and don't forget to join us in the [LiveKit Community Slack](https://livekit.io/join-slack)!

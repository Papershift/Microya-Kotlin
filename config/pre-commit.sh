RED="\033[0;31m"
YELLOW="\033[1;33m"
GREEN="\033[0;32m"
RESET="\033[0m"

if ! [ -x "$(command -v brew)" ]; then
  echo "$YELLOW[config/pre-commit.sh]: Installing Homebrew ...$RESET"
  /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
fi

if ! [ -x "$(command -v swift-sh)" ]; then
  echo "$YELLOW[config/pre-commit.sh]: Installing swift-sh ...$RESET"
  brew install swift-sh
fi

if ! [ -x "$(command -v anylint)" ]; then
  echo "$YELLOW[config/pre-commit.sh]: Installing AnyLint ...$RESET"
  brew tap Flinesoft/AnyLint https://github.com/Flinesoft/AnyLint.git
  brew install anylint
fi

if [ "/Applications/Xcode.app/Contents/Developer" != $(xcode-select -p) ]; then
  echo "$YELLOW[config/pre-commit.sh]: Setting the Xcode developer path ...$RESET"
  sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
fi

echo "$YELLOW[config/pre-commit.sh]: Configuring git pre-commit hook ...$RESET"
mkdir -p .git/hooks
touch .git/hooks/pre-commit
echo "./gradlew detekt && anylint" > .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

echo "$YELLOW[config/pre-commit.sh]: Configuration was successful! 🎉$RESET"

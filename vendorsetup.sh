CL_YLW="\033[1;33m"
CL_GRN="\033[1;32m"
CL_RST="\033[0m"

  read -rp "Would you like to compile with OOSCAM? [y|n]: " choice

  case ${choice} in
  Y | y) export TARGET_SHIPS_OOSCAM=true && echo -e ${CL_GRN}"Enabled"${CL_RST} ;;
  N | n) export TARGET_SHIPS_OOSCAM=false && echo -e ${CL_YLW}"Enjoy snap cam, normie :P"${CL_RST} ;;
  esac

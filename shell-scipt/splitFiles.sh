charcount=$(cat $1 | wc -c)
if [ $charcount -lt 10000 ]
then
split -l $(($(cat $1 | wc -l)/4)) $1 split_
else
split -l $(($(cat $1 | wc -l)/16)) $1 split_
fi

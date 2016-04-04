import re
#main code

def GetTranscription(input):
	regex = re.compile("([a-zA-Z]{0,3} .*)+")
	trans = []
	for l in input:
		 #this is the regex
		m = regex.match(l)
		if m is not None:
			trans.append(m.group(0)) #append the transcription to the list 
	return trans
def getTranscription2(input):
	regex = re.compile("([a-zA-Z]{0,3} .*)+")
	searchR = regex.search(input)
	blank = ""	
	if searchR is not None:
		return input[searchR.start():(len(input)-1)]
	return blank
#main code

#finished getting transcriptions
class Word(object):
    def __init__(self, line):
        self.ortho = line[0:line.index('\t')]
        self.lastChar = line[len(line)-1]
        self.trans = getTranscription2(line)
#def makeWord(line):
	#w = Word()
#	w.ortho = line[0:line.index('\t')]
#	w.lastChar = line[len(line)-1]
#	w.trans = getTranscription2(line)
#	return w

def getStops(input):
	regex = re.compile("([A-Z a-z])+[tpkbdg] *$") #look for just word-final voiceless stops
	m = regex.match(input)

	if m is not None:
		match = m.group(0)
		lastChar = match[len(match)-1]
		penult = match[len(match)-2]
	if m is not None and not (lastChar =='g' and penult == 'n'):
		return True
	return False

def firstC(w):
	i=0
	o = w.ortho
	while not str.isAlpha(o[i]) and i<len(0):
		i = i+1
	return o[i]


def Confirm2(line2, trans):
	root = Word(line2)
	oEnd = root.ortho[len(root.ortho)-1]
	#just look at individual word objects, if oEnd is voiced and trans end is not
	#start by looking for voicelss stops in the transcription
	matching = []
	newWord = root.trans[0:len(root.trans)-1]
	newRegex = ""
	endRegex = ""
	if oEnd == 'd':
		endRegex = "t.*" #if ortho end in d look for words ending in t
	elif oEnd == 'b':
		endRegex = "p.*"
	elif oEnd == 'g':
		endRegex = "k.*"
	newRegex = ".*" + newWord + endRegex
	for t in trans:
		match = re.match(newRegex, t)
		if match is not None and firstC(root) == t[0]:
			matching.append(t)
	sorted(matching, key=lambda x: -1*len(x))
	secondRegex = newRegex[0:len(newRegex)-2] + " ?[aieou]"
	for m in matching:
		nMatch = re.match(newRegex, m)
		if nMatch is not None:
			return endRegex[0:len(endRegex)-2]
	return "" +oEnd




def main(path):
	with open(path, "r") as f:
		lines = f.readlines() #works
	excludeRegex=re.compile(r".*\w$") #.*[^0-9<>'+.,]
	#lengthRegex=re.compile(".(([\t ][A-Za-z]+ *)+){2,}") #at least 3 segments
	with open("/Users/Elias/Desktop/printFile.txt", "w") as f2:



		trans = GetTranscription(lines)
		for l in lines :
			w = Word(l)
	#print(w.ortho)
			orthoMatch = excludeRegex.match(w.ortho.strip())
		#	transMatch = lengthRegex.match(w.trans) #and transMatch is not None
	#print(w.trans)
			if getStops(w.ortho) and (orthoMatch is not None ):
				w.underlying = Confirm2(l, trans)		
				
				f2.write(w.ortho + "\t" + w.underlying + "\n")

		 

if __name__ == '__main__':
	path = "/Users/Elias/Desktop/LAB/GE.txt"
	main(path)

#argparse


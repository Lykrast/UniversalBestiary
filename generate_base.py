#Common functions
def validCategory(cat):
	category = cat.lower()
	return category == 'a' or category == 'animals' or category == 'h' or category == 'helpers' or category == 'm' or category == 'monsters' or category == 'b' or category == 'bosses'

def convertCategory(cat):
	category = cat.lower()
	if category == 'a' or category == 'animals':
		return 'animals'
	elif category == 'h' or category == 'helpers':
		return 'helpers'
	elif category == 'm' or category == 'monsters':
		return 'monsters'
	elif category == 'b' or category == 'bosses':
		return 'bosses'
	else:
		raise ValueError('Invalid category {}. Must be a (animals), h (helpers), m (monsters) or b (bosses).'.format(category))

def readTemplate(fileName):
	content = ''
	with open('./templates/{}.json'.format(fileName), 'r') as file_tmp:
		content = file_tmp.read()
	return content

def regToFilename(registry):
	return registry.replace(':', '_', 1)